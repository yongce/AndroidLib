package me.ycdev.android.lib.common.net

import com.google.common.truth.Truth.assertThat
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.io.ByteArrayOutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.util.concurrent.Executors
import java.util.zip.DeflaterOutputStream
import java.util.zip.GZIPOutputStream
import me.ycdev.android.lib.common.utils.LibLogger
import org.junit.After
import org.junit.Before
import org.junit.Test

class HttpClientTest {
    private lateinit var server: HttpServer
    private lateinit var baseUrl: String

    @Before
    fun setUp() {
        LibLogger.enableJvmLogger()
        server = HttpServer.create(InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0)
        server.executor = Executors.newSingleThreadExecutor()
        server.start()
        baseUrl = "http://127.0.0.1:${server.address.port}"
    }

    @After
    fun tearDown() {
        server.stop(0)
    }

    @Test
    fun get_sendsHeadersAndReturnsResponseBody() {
        server.createContext("/get") { exchange ->
            assertThat(exchange.requestMethod).isEqualTo("GET")
            assertThat(exchange.requestHeaders.getFirst("X-Test")).isEqualTo("yes")
            exchange.sendText(200, "query=${exchange.requestURI.query}")
        }

        val response = HttpClient().get("$baseUrl/get?name=value", hashMapOf("X-Test" to "yes"))

        assertThat(response).isEqualTo("query=name=value")
    }

    @Test
    fun post_sendsStringBodyAndReturnsResponseBody() {
        server.createContext("/post-string") { exchange ->
            assertThat(exchange.requestMethod).isEqualTo("POST")
            val body = exchange.requestBody.readBytes().toString(Charsets.UTF_8)
            exchange.sendText(200, "body=$body")
        }

        val response = HttpClient().post("$baseUrl/post-string", "hello")

        assertThat(response).isEqualTo("body=hello")
    }

    @Test
    fun post_sendsByteArrayBodyAndReturnsResponseBody() {
        server.createContext("/post-bytes") { exchange ->
            assertThat(exchange.requestMethod).isEqualTo("POST")
            val body = exchange.requestBody.readBytes()
            exchange.sendText(200, body.joinToString(separator = ","))
        }

        val response = HttpClient().post("$baseUrl/post-bytes", byteArrayOf(1, 2, 3))

        assertThat(response).isEqualTo("1,2,3")
    }

    @Test
    fun get_decodesGzipResponse() {
        server.createContext("/gzip") { exchange ->
            exchange.sendBytes(200, gzip("compressed"), "gzip")
        }

        val response = HttpClient().get("$baseUrl/gzip", hashMapOf())

        assertThat(response).isEqualTo("compressed")
    }

    @Test
    fun get_decodesDeflateResponse() {
        server.createContext("/deflate") { exchange ->
            exchange.sendBytes(200, deflate("compressed"), "deflate")
        }

        val response = HttpClient().get("$baseUrl/deflate", hashMapOf())

        assertThat(response).isEqualTo("compressed")
    }

    @Test
    fun get_returnsErrorStreamBodyForHttpError() {
        server.createContext("/error") { exchange ->
            exchange.sendText(400, "bad request")
        }

        val response = HttpClient().get("$baseUrl/error", hashMapOf())

        assertThat(response).isEqualTo("bad request")
    }

    private fun HttpExchange.sendText(statusCode: Int, body: String) {
        sendBytes(statusCode, body.toByteArray(Charsets.UTF_8), null)
    }

    private fun HttpExchange.sendBytes(
        statusCode: Int,
        body: ByteArray,
        contentEncoding: String?
    ) {
        if (contentEncoding != null) {
            responseHeaders.add("Content-Encoding", contentEncoding)
        }
        sendResponseHeaders(statusCode, body.size.toLong())
        responseBody.use { it.write(body) }
    }

    private fun gzip(text: String): ByteArray {
        val output = ByteArrayOutputStream()
        GZIPOutputStream(output).use { it.write(text.toByteArray(Charsets.UTF_8)) }
        return output.toByteArray()
    }

    private fun deflate(text: String): ByteArray {
        val output = ByteArrayOutputStream()
        DeflaterOutputStream(output).use { it.write(text.toByteArray(Charsets.UTF_8)) }
        return output.toByteArray()
    }
}
