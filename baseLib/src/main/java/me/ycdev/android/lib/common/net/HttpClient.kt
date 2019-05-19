package me.ycdev.android.lib.common.net

import me.ycdev.android.lib.common.utils.IoUtils
import me.ycdev.android.lib.common.utils.LibLogger
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.util.HashMap
import java.util.zip.GZIPInputStream
import java.util.zip.InflaterInputStream

@Suppress("unused")
class HttpClient {
    private val charset = "UTF-8"
    private var connectTimeout: Int = 10_000 // ms
    private var readTimeout: Int = 10_1000 // ms

    fun setTimeout(connectTimeout: Int, readTimeout: Int) {
        this.connectTimeout = connectTimeout
        this.readTimeout = readTimeout
    }

    @Throws(IOException::class)
    operator fun get(
        url: String,
        requestHeaders: HashMap<String, String>
    ): String {
        val httpConn = getHttpConnection(url, false, requestHeaders)
        try {
            httpConn.connect()
        } catch (e: Exception) {
            throw IOException(e.toString())
        }

        try {
            return getResponse(httpConn)
        } finally {
            httpConn.disconnect()
        }
    }

    @Throws(IOException::class)
    fun post(url: String, body: String): String {
        val httpConn = getHttpConnection(url, true, null)
        var os: DataOutputStream? = null

        // Send the "POST" request
        try {
            os = DataOutputStream(httpConn.outputStream)
            os.write(body.toByteArray(charset(charset)))
            os.flush()
            return getResponse(httpConn)
        } catch (e: Exception) {
            // should not be here, but.....
            throw IOException(e.toString())
        } finally {
            // Must be called before calling HttpURLConnection.disconnect()
            IoUtils.closeQuietly(os)
            httpConn.disconnect()
        }
    }

    @Throws(IOException::class)
    fun post(url: String, body: ByteArray): String {
        val httpConn = getHttpConnection(url, true, null)
        var os: DataOutputStream? = null

        // Send the "POST" request
        try {
            os = DataOutputStream(httpConn.outputStream)
            os.write(body)
            os.flush()
            return getResponse(httpConn)
        } catch (e: Exception) {
            // prepare for any unexpected exceptions
            throw IOException(e.toString())
        } finally {
            // Must be called before calling HttpURLConnection.disconnect()
            IoUtils.closeQuietly(os)
            httpConn.disconnect()
        }
    }

    @Throws(IOException::class)
    private fun getHttpConnection(
        url: String,
        post: Boolean,
        requestHeaders: HashMap<String, String>?
    ): HttpURLConnection {
        val httpConn = NetworkUtils.openHttpURLConnection(url)
        httpConn.connectTimeout = connectTimeout
        httpConn.readTimeout = readTimeout
        httpConn.doInput = true
        httpConn.useCaches = false
        httpConn.setRequestProperty("Accept-Encoding", "gzip,deflate")
        httpConn.setRequestProperty("Charset", charset)
        if (requestHeaders != null) {
            addRequestHeaders(httpConn, requestHeaders)
        }
        if (post) {
            httpConn.doOutput = true
            httpConn.requestMethod = "POST"
        } else {
            httpConn.requestMethod = "GET" // by default
        }
        return httpConn
    }

    private fun addRequestHeaders(
        httpConn: HttpURLConnection,
        requestHeaders: HashMap<String, String>
    ) {
        val allHeaders = requestHeaders.entries
        for ((key, value) in allHeaders) {
            httpConn.addRequestProperty(key, value)
        }
    }

    @Throws(IOException::class)
    private fun getResponse(httpConn: HttpURLConnection): String {
        val contentEncoding = httpConn.contentEncoding
        LibLogger.d(
            TAG, "response code: " + httpConn.responseCode +
                    ", encoding: " + contentEncoding + ", method: " + httpConn.requestMethod
        )

        var httpInputStream: InputStream? = null
        try {
            httpInputStream = httpConn.inputStream
        } catch (e: IOException) {
            // ignore
        } catch (e: IllegalStateException) {
        }

        if (httpInputStream == null) {
            // If httpConn.getInputStream() throws IOException,
            // we can get the error message from the error stream.
            // For example, the case when the response code is 4xx.
            httpInputStream = httpConn.errorStream
        }
        if (httpInputStream == null) {
            throw IOException("HttpURLConnection.getInputStream() returned null")
        }

        val input: InputStream
        if (contentEncoding != null && contentEncoding.contains("gzip")) {
            input = GZIPInputStream(httpInputStream)
        } else if (contentEncoding != null && contentEncoding.contains("deflate")) {
            input = InflaterInputStream(httpInputStream)
        } else {
            input = httpInputStream
        }

        // Read the response content
        try {
            val responseContent = input.readBytes()
            return String(responseContent, charset(charset))
        } finally {
            // Must be called before calling HttpURLConnection.disconnect()
            IoUtils.closeQuietly(input)
        }
    }

    companion object {
        private const val TAG = "HttpClient"
    }
}
