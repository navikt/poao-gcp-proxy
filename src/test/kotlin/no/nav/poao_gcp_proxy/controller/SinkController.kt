package no.nav.poao_gcp_proxy.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/sink")
class SinkController {

	@RequestMapping("/**")
	fun test(httpRequest: HttpServletRequest): SinkResponse {
		println("${httpRequest.method} ${httpRequest.requestURL}")

		return SinkResponse(
			method = httpRequest.method,
			url = httpRequest.requestURL.toString(),
			authHeader = httpRequest.getHeader("Authorization")
		)
	}

	data class SinkResponse(
		val method: String,
		val url: String,
		val authHeader: String
	)

}
