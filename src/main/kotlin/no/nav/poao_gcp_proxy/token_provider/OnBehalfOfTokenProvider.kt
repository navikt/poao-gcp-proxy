package no.nav.poao_gcp_proxy.token_provider

interface OnBehalfOfTokenProvider {

	fun getOnBehalfOfToken(scope: String, accessToken: String): String

}
