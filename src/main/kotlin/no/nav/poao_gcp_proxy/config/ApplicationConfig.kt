package no.nav.poao_gcp_proxy.config

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!local")
@EnableJwtTokenValidation
@EnableConfigurationProperties(EnvironmentProperties::class)
class ApplicationConfig {

}
