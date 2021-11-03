package no.nav.poao_gcp_proxy.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "proxy")
data class ProxyConfig(
	var proxies: List<ProxyTarget> = emptyList()
)

data class ProxyTarget(
	var appName: String = "",
	var appNamespace: String = "",
	var appCluster: String = "",
)
