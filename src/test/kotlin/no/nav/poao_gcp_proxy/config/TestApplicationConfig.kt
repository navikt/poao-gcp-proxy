package no.nav.poao_gcp_proxy.config

import no.nav.poao_gcp_proxy.proxy_filter.PreRequestZuulFilter
import no.nav.poao_gcp_proxy.token_provider.ScopedTokenProvider
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("test")
@Configuration
@EnableZuulProxy
@EnableConfigurationProperties(ProxyConfig::class)
class TestApplicationConfig {

	@Bean
	fun scopedTokenProvider(): ScopedTokenProvider {
		return object : ScopedTokenProvider {
			override fun getToken(scope: String): String {
				return "SCOPED_TOKEN"
			}
		}
	}

	@Bean
	fun preRequestZuulFilter(proxyConfig: ProxyConfig, scopedTokenProvider: ScopedTokenProvider): PreRequestZuulFilter {
		return PreRequestZuulFilter("/proxy", proxyConfig, scopedTokenProvider)
	}

}
