package no.nav.poao_gcp_proxy.proxy_filter

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
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

		log.info("Proxy response: status=${response.status}method=${request.method} fromUrl=${request.requestURL} toEndpoint=${ctx.routeHost}")

		return null
	}

}
