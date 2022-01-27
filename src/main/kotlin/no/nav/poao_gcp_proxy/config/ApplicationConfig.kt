package no.nav.poao_gcp_proxy.config

import no.nav.poao_gcp_proxy.proxy_filter.PostRequestZuulFilter
import no.nav.poao_gcp_proxy.proxy_filter.PreRequestZuulFilter
import no.nav.security.token.support.core.configuration.MultiIssuerConfiguration
import no.nav.security.token.support.core.validation.JwtTokenValidationHandler
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("default")
@Configuration
@EnableZuulProxy
@EnableJwtTokenValidation
class ApplicationConfig {

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

}
