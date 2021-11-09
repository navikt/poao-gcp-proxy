package no.nav.poao_gcp_proxy.config

import no.nav.poao_gcp_proxy.proxy_filter.PreRequestZuulFilter
import no.nav.poao_gcp_proxy.token_provider.ScopedTokenProvider
import no.nav.poao_gcp_proxy.token_provider.azure_ad.AzureAdScopedTokenProviderBuilder
import no.nav.security.token.support.core.configuration.MultiIssuerConfiguration
import no.nav.security.token.support.core.validation.JwtTokenValidationHandler
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("default")
@Configuration
@EnableZuulProxy
@EnableJwtTokenValidation
@EnableConfigurationProperties(ProxyConfig::class)
class ApplicationConfig {

	@Bean
	fun scopedTokenProvider(): ScopedTokenProvider {
		return AzureAdScopedTokenProviderBuilder.builder()
			.withEnvironmentDefaults()
			.build()
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
