package no.nav.poao_gcp_proxy.token_provider

import com.nimbusds.oauth2.sdk.*
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic
import com.nimbusds.oauth2.sdk.auth.Secret
import com.nimbusds.oauth2.sdk.id.ClientID
import com.nimbusds.oauth2.sdk.token.BearerAccessToken
import com.nimbusds.oauth2.sdk.token.TokenTypeURI
import com.nimbusds.oauth2.sdk.tokenexchange.TokenExchangeGrant
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.URI

// This has not been tested at all

class AzureAdOnBehalfOfTokenProvider(clientId: String, clientSecret: String, tokenEndpointUrl: String) : OnBehalfOfTokenProvider {

	private val log = LoggerFactory.getLogger(AzureAdOnBehalfOfTokenProvider::class.java)
	private val clientAuth: ClientAuthentication
	private val tokenEndpoint: URI

	init {
		clientAuth = ClientSecretBasic(ClientID(clientId), Secret(clientSecret))
		tokenEndpoint = URI(tokenEndpointUrl)
	}

	override fun getOnBehalfOfToken(scope: String, accessToken: String): String {
		val tokenExchangeGrant = TokenExchangeGrant(BearerAccessToken(accessToken), TokenTypeURI.ACCESS_TOKEN)
		val request = TokenRequest(tokenEndpoint, clientAuth, tokenExchangeGrant, Scope(scope))

		var response: TokenResponse? = null

		try {
			response = TokenResponse.parse(request.toHTTPRequest().send())
		} catch (e: ParseException) {
			log.error("Failed to parse JWT token", e)
		} catch (e: IOException) {
			log.error("Failed to send token request", e)
		}

		checkNotNull(response) { "Failed to get token" }

		if (!response.indicatesSuccess()) {
			val tokenErrorResponse = response.toErrorResponse()
			log.error("Token request was not successful", tokenErrorResponse.toJSONObject().toString())
			throw RuntimeException("Failed to fetch service token for $scope")
		}

		val successResponse = response.toSuccessResponse()

		return successResponse.tokens.accessToken.value
	}

}
