package no.nav.poao_gcp_proxy.proxy_filter

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import com.netflix.zuul.exception.ZuulException
import no.nav.security.token.support.core.http.HttpRequest
import no.nav.security.token.support.core.validation.JwtTokenValidationHandler
import org.slf4j.LoggerFactory

class PreRequestZuulFilter(
	private val jwtTokenValidationHandler: JwtTokenValidationHandler
) : ZuulFilter() {

	companion object {
		const val DOWNSTREAM_AUTHORIZATION_HEADER = "downstream-authorization"
		const val AUTHORIZATION_HEADER = "authorization"
	}

	private val log = LoggerFactory.getLogger(this::class.java)

	override fun shouldFilter() = true

	override fun filterType() = "pre"

	override fun filterOrder() = 1

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
			log.warn("Request is not authenticated")
			throw ZuulException("Request is not authenticated", 401, "Token is missing or invalid")
		}

		val downstreamAuthorizationHeader: String? = request.getHeader(DOWNSTREAM_AUTHORIZATION_HEADER)

		ctx.addZuulRequestHeader(DOWNSTREAM_AUTHORIZATION_HEADER, null)
		ctx.addZuulRequestHeader(AUTHORIZATION_HEADER, downstreamAuthorizationHeader)

		return null
	}

}
