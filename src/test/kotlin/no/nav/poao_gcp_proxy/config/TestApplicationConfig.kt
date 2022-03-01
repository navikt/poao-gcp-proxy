package no.nav.poao_gcp_proxy.config

import no.nav.common.rest.filter.ConsumerIdComplianceFilter
import no.nav.poao_gcp_proxy.proxy_filter.PostRequestZuulFilter
import no.nav.poao_gcp_proxy.proxy_filter.PreRequestZuulFilter
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.token.support.core.configuration.MultiIssuerConfiguration
import no.nav.security.token.support.core.validation.JwtTokenValidationHandler
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile


@Profile("test")
@Configuration
@EnableZuulProxy
@EnableJwtTokenValidation
class TestApplicationConfig {

	companion object {
		var mockOAuth2Server = MockOAuth2Server()

		init {
			mockOAuth2Server.start()
			System.setProperty("MOCK_AZURE_AD_DISCOVERY_URL", mockOAuth2Server.wellKnownUrl("azuread").toString())
		}
	}

	@Bean
	fun preRequestZuulFilter(jwtTokenValidationHandler: JwtTokenValidationHandler): PreRequestZuulFilter {
		return PreRequestZuulFilter(jwtTokenValidationHandler)
	}

	@Bean
	fun postRequestZuulFilter(): PostRequestZuulFilter {
		return PostRequestZuulFilter()
	}

	@Bean
	fun jwtTokenValidationHandler(config: MultiIssuerConfiguration): JwtTokenValidationHandler {
		return JwtTokenValidationHandler(config)
	}

	@Bean
	fun complianceFilterRegistrationBean(): FilterRegistrationBean<ConsumerIdComplianceFilter> {
		val registration = FilterRegistrationBean<ConsumerIdComplianceFilter>()
		registration.filter = ConsumerIdComplianceFilter(true)
		registration.order = 0
		registration.addUrlPatterns("/proxy/*")
		return registration
	}

}
