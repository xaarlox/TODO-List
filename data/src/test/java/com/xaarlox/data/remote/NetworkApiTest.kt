package com.xaarlox.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertThrows
import org.junit.Test

class NetworkApiTest {
    @Test
    fun `should return IP string when server responds with 200 OK`() = runBlocking {
        val mockEngine = MockEngine { _: HttpRequestData ->
            respond(
                content = "15.03.20.25",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "text/plain")
            )
        }
        val client = HttpClient(mockEngine)
        val api = NetworkApiImpl(client)

        val result = api.getUserIp()
        assertEquals("15.03.20.25", result)
    }

    @Test
    fun `should throw ClientRequestException when server responds with 400 Bad Request`(): Unit =
        runBlocking {
            val mockEngine = MockEngine { _: HttpRequestData ->
                respond(
                    content = "Bad Request",
                    status = HttpStatusCode.BadRequest,
                    headers = headersOf(HttpHeaders.ContentType, "text/plain")
                )
            }
            val client = HttpClient(mockEngine) {
                expectSuccess = true
            }
            val api = NetworkApiImpl(client)

            assertThrows(ClientRequestException::class.java) {
                runBlocking { api.getUserIp() }
            }
        }

    @Test
    fun `should throw ServerResponseException when server responds with 500 Internal Server Error`(): Unit =
        runBlocking {
            val mockEngine = MockEngine { _: HttpRequestData ->
                respond(
                    content = "Internal Server Error",
                    status = HttpStatusCode.InternalServerError,
                    headers = headersOf(HttpHeaders.ContentType, "text/plain")
                )
            }
            val client = HttpClient(mockEngine) {
                expectSuccess = true
            }
            val api = NetworkApiImpl(client)

            assertThrows(ServerResponseException::class.java) {
                runBlocking { api.getUserIp() }
            }
        }

    @Test
    fun `should return empty string when server responds with 200 OK and empty body`() =
        runBlocking {
            val mockEngine = MockEngine { _: HttpRequestData ->
                respond(
                    content = "",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "text/plain")
                )
            }
            val client = HttpClient(mockEngine)
            val api = NetworkApiImpl(client)

            val result = api.getUserIp()
            assertEquals("", result)
        }
}