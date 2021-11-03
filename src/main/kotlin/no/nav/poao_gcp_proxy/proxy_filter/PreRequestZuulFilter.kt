package no.nav.poao_gcp_proxy.proxy_filter

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import com.netflix.zuul.exception.ZuulException
import no.nav.poao_gcp_proxy.config.ProxyConfig
import no.nav.poao_gcp_proxy.token_provider.ScopedTokenProvider
import no.nav.poao_gcp_proxy.utils.UrlUtils

class PreRequestZuulFilter(
	private val contextPath: String,
	private val proxyConfig: ProxyConfig,
	private val scopedTokenProvider: ScopedTokenProvider
) : ZuulFilter() {

	override fun shouldFilter(): Boolean {
		return true
	}

	override fun run(): Any? {
		val ctx = RequestContext.getCurrentContext()
		val request = ctx.request

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
