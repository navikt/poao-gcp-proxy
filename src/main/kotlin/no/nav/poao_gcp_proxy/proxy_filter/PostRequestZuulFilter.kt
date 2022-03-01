package no.nav.poao_gcp_proxy.proxy_filter

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import no.nav.common.log.LogFilter
import org.slf4j.LoggerFactory

class PostRequestZuulFilter : ZuulFilter() {

	private val log = LoggerFactory.getLogger(this::class.java)

	override fun shouldFilter() = true

	override fun filterType() = "post"

	override fun filterOrder() = 100

	override fun run(): Any? {
		val ctx = RequestContext.getCurrentContext()
		val request = ctx.request
		val response = ctx.response
		val requestingApp = ctx.request.getHeader(LogFilter.CONSUMER_ID_HEADER_NAME) ?: "unknown"

		log.info("Proxy response: status=${response.status} method=${request.method} fromApp=${requestingApp} requestUrl=${request.requestURL} toRoute=${ctx.routeHost}")

		return null
	}

}
