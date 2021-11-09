package no.nav.poao_gcp_proxy.proxy_filter

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import com.netflix.zuul.exception.ZuulException
import no.nav.poao_gcp_proxy.config.ProxyConfig
import no.nav.poao_gcp_proxy.token_provider.ScopedTokenProvider
import no.nav.poao_gcp_proxy.utils.UrlUtils
import no.nav.security.token.support.core.http.HttpRequest
import no.nav.security.token.support.core.validation.JwtTokenValidationHandler

class PreRequestZuulFilter(
	private val contextPath: String,
	private val proxyConfig: ProxyConfig,
	private val scopedTokenProvider: ScopedTokenProvider,
	private val jwtTokenValidationHandler: JwtTokenValidationHandler
) : ZuulFilter() {

	override fun shouldFilter(): Boolean {
		return true
	}

	override fun run(): Any? {
		val ctx = RequestContext.getCurrentContext()
		val request = ctx.request

		val tokens = jwtTokenValidationHandler.getValidatedTokens(object : HttpRequest {
			override fun getHeader(headerName: String?): String {
				return request.getHeader(headerName) ?: ""
			}

			override fun getCookies(): Array<HttpRequest.NameValue> {
				if (request.cookies == null) {
					return emptyArray()
				}

				return request.cookies.iterator().asSequence().toList().map {
					return@map object : HttpRequest.NameValue {
						override fun getName(): String {
							return it.name
						}

						override fun getValue(): String {
							return it.value ?: ""
						}
					}
				}.toTypedArray()
			}
		})

		if (!tokens.hasValidToken()) {
			throw ZuulException("Request is not authenticated", 401, "Token is missing or invalid")
		}

		val pathWithoutContext: String = UrlUtils.stripStartPath(contextPath, request.requestURI)
		val appName: String = UrlUtils.getFirstSegment(pathWithoutContext)

		val proxy = proxyConfig.proxies.find { it.appName == appName }
			?: throw ZuulException("Proxy Mapping Not Found", 404, "This endpoint is not a proxy")

		// TODO: Basert på hvilket token som kommer inn, så burde vi lage nytt token å sende videre
		//		F.eks TokenX, eller AzureAD OBO

		val token = scopedTokenProvider.getToken(createScope(proxy.appCluster, proxy.appNamespace, proxy.appName))

		ctx.addZuulRequestHeader("Authorization", "Bearer $token")

		return null
	}

	override fun filterType(): String {
		return "pre"
	}

	override fun filterOrder(): Int {
		return 1
	}

	private fun createScope(appCluster: String, appNamespace: String, appName: String): String {
		return "api://$appCluster.$appNamespace.$appName/.default"
	}

}
