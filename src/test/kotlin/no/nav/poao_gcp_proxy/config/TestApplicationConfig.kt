package no.nav.poao_gcp_proxy.config

import no.nav.poao_gcp_proxy.proxy_filter.PreRequestZuulFilter
import no.nav.poao_gcp_proxy.token_provider.ScopedTokenProvider
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.token.support.core.configuration.MultiIssuerConfiguration
import no.nav.security.token.support.core.validation.JwtTokenValidationHandler
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile


@Profile("test")
@Configuration
@EnableZuulProxy
@EnableJwtTokenValidation
@EnableConfigurationProperties(ProxyConfig::class)
class TestApplicationConfig {

	companion object {
		var mockOAuth2Server = MockOAuth2Server()

		init {
			mockOAuth2Server.start()
			System.setProperty("MOCK_AZURE_AD_DISCOVERY_URL", mockOAuth2Server.wellKnownUrl("azuread").toString())
		}
	}

	@Bean
	fun scopedTokenProvider(): ScopedTokenProvider {
		return object : ScopedTokenProvider {
			override fun getToken(scope: String): String {
				return "SCOPED_TOKEN"
			}
		}
	}

	@Bean
	fun preRequestZuulFilter(
		proxyConfig: ProxyConfig,
		scopedTokenProvider: ScopedTokenProvider,
		jwtTokenValidationHandler: JwtTokenValidationHandler
	): PreRequestZuulFilter {
		return PreRequestZuulFilter("/proxy", proxyConfig, scopedTokenProvider, jwtTokenValidationHandler)
	}

	@Bean
	fun jwtTokenValidationHandler(config: MultiIssuerConfiguration): JwtTokenValidationHandler {
		return JwtTokenValidationHandler(config)
	}

}
