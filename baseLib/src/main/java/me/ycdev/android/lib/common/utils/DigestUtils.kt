package me.ycdev.android.lib.common.utils

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import me.ycdev.android.lib.common.utils.EncodingUtils.encodeWithHex

@Suppress("unused")
object DigestUtils {
    @Throws(NoSuchAlgorithmException::class, UnsupportedEncodingException::class)
    fun md5(text: String): String {
        return hash(text, "MD5")
    }

    @Throws(NoSuchAlgorithmException::class)
    fun md5(data: ByteArray): String {
        return hash(data, "MD5")
    }

    @Throws(NoSuchAlgorithmException::class, UnsupportedEncodingException::class)
    fun sha1(text: String): String {
        return hash(text, "SHA-1")
    }

    @Throws(NoSuchAlgorithmException::class)
    fun sha1(data: ByteArray): String {
        return hash(data, "SHA-1")
    }

    @Throws(NoSuchAlgorithmException::class, UnsupportedEncodingException::class)
    fun hash(text: String, algorithm: String): String {
        return hash(text.toByteArray(charset("UTF-8")), algorithm)
    }

    @Throws(NoSuchAlgorithmException::class)
    fun hash(data: ByteArray, algorithm: String): String {
        val digest = MessageDigest.getInstance(algorithm)
        digest.update(data)
        val messageDigest = digest.digest()
        return encodeWithHex(messageDigest, false)
    }

    /**
     * The caller should close the stream.
     */
    @Throws(NoSuchAlgorithmException::class, IOException::class)
    fun md5(stream: InputStream?): String {
        if (stream == null) {
            throw IllegalArgumentException("Invalid input stream!")
        }
        val buffer = ByteArray(1024)
        val complete = MessageDigest.getInstance("MD5")
        var numRead: Int
        do {
            numRead = stream.read(buffer)
            if (numRead > 0) {
                complete.update(buffer, 0, numRead)
            }
        } while (numRead != -1)
        val digest = complete.digest()
        return encodeWithHex(digest, false)
    }

    @Throws(NoSuchAlgorithmException::class, IOException::class)
    fun md5(file: File): String {
        var stream: FileInputStream? = null
        try {
            stream = FileInputStream(file)
            return md5(stream)
        } finally {
            IoUtils.closeQuietly(stream)
        }
    }
}
