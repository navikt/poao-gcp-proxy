package no.nav.poao_gcp_proxy.controller

import no.nav.security.token.support.core.api.Unprotected
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/sink")
class SinkController {

	@Unprotected
	@RequestMapping("/**")
	fun test(httpRequest: HttpServletRequest): SinkResponse {
		println("${httpRequest.method} ${httpRequest.requestURL}")

		return SinkResponse(
			method = httpRequest.method,
			url = httpRequest.requestURL.toString(),
			authHeader = httpRequest.getHeader("Authorization"),
			downstreamAuthHeader = httpRequest.getHeader("Downstream-Authorization")
		)
	}

	data class SinkResponse(
		val method: String,
		val url: String,
		val authHeader: String?,
		val downstreamAuthHeader: String?
	)

}
