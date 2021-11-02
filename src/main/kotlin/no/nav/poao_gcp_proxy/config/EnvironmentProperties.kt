package no.nav.poao_gcp_proxy.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.env")
data class EnvironmentProperties (
	var arenaOrdsUrl: String = "",
	var arenaOrdsClientId: String = "",
	var arenaOrdsClientSecret: String = "",
)
