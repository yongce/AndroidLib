package me.ycdev.android.lib.common.utils

import java.io.Closeable
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipFile

@Suppress("unused")
object IoUtils {
    /**
     * Close the closeable target and eat possible exceptions.
     * @param target The target to close. Can be null.
     */
    fun closeQuietly(target: Closeable?) {
        try {
            target?.close()
        } catch (e: Exception) {
            // ignore
        }
    }

    /**
     * Before Android 4.4, ZipFile doesn't implement the interface "java.io.Closeable".
     * @param target The target to close. Can be null.
     */
    fun closeQuietly(target: ZipFile?) {
        try {
            target?.close()
        } catch (e: IOException) {
            // ignore
        }
    }

    @Deprecated("Not needed anymore", ReplaceWith("Use InputStream#readBytes()"))
    @Throws(IOException::class)
    fun readAllBytes(input: InputStream): ByteArray {
        return input.readBytes()
    }

    /**
     * Read all lines of the stream as a String.
     * Use the "UTF-8" character converter when reading.
     * @return May be empty String, but never null.
     */
    @Throws(IOException::class)
    fun readAllLines(input: InputStream): String {
        return input.bufferedReader().use { it.readText() }
    }

    /**
     * Read all lines of the text file as a String.
     * @param filePath The file to read
     */
    @Throws(IOException::class)
    fun readAllLines(filePath: String): String {
        val fis = FileInputStream(filePath)
        try {
            return fis.bufferedReader().readText()
        } finally {
            closeQuietly(fis)
        }
    }

    /**
     * @param lineNumber Start from 1
     */
    @Throws(IOException::class)
    fun readOneLine(input: InputStream, lineNumber: Int): String? {
        val reader = input.bufferedReader()
        var line: String? = null

        for (i in 0 until lineNumber) {
            line = reader.readLine()
            if (line == null) {
                break
            }
        }

        return line
    }

    /**
     * @param lineNumber Start from 1
     */
    @Throws(IOException::class)
    fun readOneLine(filePath: String, lineNumber: Int): String? {
        val fis = FileInputStream(filePath)
        try {
            return readOneLine(fis, lineNumber)
        } finally {
            closeQuietly(fis)
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun createParentDirsIfNeeded(file: File) {
        val dirFile = file.parentFile
        if (dirFile != null && !dirFile.exists()) {
            dirFile.mkdirs()
        }
    }

    fun createParentDirsIfNeeded(filePath: String) {
        createParentDirsIfNeeded(File(filePath))
    }

    @Throws(IOException::class)
    fun saveAsFile(content: String, filePath: String) {
        val fw = FileWriter(filePath)
        try {
            fw.write(content)
            fw.flush()
        } finally {
            closeQuietly(fw)
        }
    }

    /**
     * Save the input stream into a file.
     * Note: This method will not close the input stream.
     */
    @Throws(IOException::class)
    fun saveAsFile(input: InputStream, filePath: String) {
        val fos = FileOutputStream(filePath)
        try {
            input.copyTo(fos)
        } finally {
            closeQuietly(fos)
        }
    }

    /**
     * Copy data from the input stream to the output stream.
     * Note: This method will not close the input stream and output stream.
     */
    @Deprecated("Not needed anymore", ReplaceWith(("Use InputStream#copyTo()")))
    @Throws(IOException::class)
    fun copyStream(input: InputStream, os: OutputStream) {
        input.copyTo(os)
    }

    @Throws(IOException::class)
    fun copyFile(srcFilePath: String, destFilePath: String) {
        val fis = FileInputStream(srcFilePath)
        try {
            saveAsFile(fis, destFilePath)
        } finally {
            closeQuietly(fis)
        }
    }
}
