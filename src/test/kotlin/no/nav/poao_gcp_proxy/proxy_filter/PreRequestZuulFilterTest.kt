package no.nav.poao_gcp_proxy.proxy_filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import no.nav.poao_gcp_proxy.config.TestApplicationConfig.Companion.mockOAuth2Server
import no.nav.poao_gcp_proxy.controller.SinkController
import no.nav.security.mock.oauth2.MockOAuth2Server
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class PreRequestZuulFilterTest {

	private val client = OkHttpClient()
	private val objectMapper = ObjectMapper().registerKotlinModule()

	@Test
	fun `should receive 401 as response if token is missing`() {
		val request = Request.Builder()
			.url("http://localhost:5678/proxy/test-app/test/hello/world?foo=bar")
			.get()
			.build()

		client.newCall(request).execute().use { response ->
			assertEquals(401, response.code)
		}
	}

	@Test
	fun `should receive 200 as response if valid token is provided`() {
		val token = mockOAuth2Server.issueToken("azuread", "test", "test").serialize()

		val request = Request.Builder()
			.url("http://localhost:5678/proxy/test-app/test/hello/world?foo=bar")
			.header("Authorization", "Bearer $token")
			.get()
			.build()

		client.newCall(request).execute().use { response ->
			assertEquals(200, response.code)
		}
	}

	@Test
	fun `should proxy request to sink with authorization header`() {
		val token = mockOAuth2Server.issueToken("azuread", "test", "test").serialize()

		val request = Request.Builder()
			.url("http://localhost:5678/proxy/test-app/test/hello/world?foo=bar")
			.header("Authorization", "Bearer $token")
			.get()
			.build()

		client.newCall(request).execute().use { response ->
			assertTrue(response.isSuccessful)

			val body = response.body?.string() ?: throw RuntimeException("Body is missing")
			val sinkResponse = objectMapper.readValue(body, SinkController.SinkResponse::class.java)

			assertEquals("GET", sinkResponse.method)
			assertEquals("http://localhost:5678/sink/test/hello/world", sinkResponse.url)
			assertEquals("Bearer SCOPED_TOKEN", sinkResponse.authHeader)
		}
	}

	@Test
	fun `should validate token for incoming requests to public proxy endpoints`() {
		val request = Request.Builder()
			.url("http://localhost:5678/proxy/public-app/test/hello/world?foo=bar")
			.get()
			.build()

		client.newCall(request).execute().use { response ->
			assertEquals(401, response.code)
		}
	}

	@Test
	fun `should not add auth header for public proxy`() {
		val token = mockOAuth2Server.issueToken("azuread", "test", "test").serialize()

		val request = Request.Builder()
			.url("http://localhost:5678/proxy/public-app/test/hello/world?foo=bar")
			.header("Authorization", "Bearer $token")
			.get()
			.build()

		client.newCall(request).execute().use { response ->
			assertTrue(response.isSuccessful)

			val body = response.body?.string() ?: throw RuntimeException("Body is missing")
			val sinkResponse = objectMapper.readValue(body, SinkController.SinkResponse::class.java)

			assertEquals("GET", sinkResponse.method)
			assertEquals("http://localhost:5678/sink/test/hello/world", sinkResponse.url)
			assertNull(sinkResponse.authHeader)
		}
	}

}
