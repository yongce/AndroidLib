package me.ycdev.android.lib.common.utils

import com.google.common.truth.Truth.assertThat
import java.io.ByteArrayInputStream
import java.io.Closeable
import java.io.File
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class IoUtilsTest {
    @Rule @JvmField
    val temporaryFolder = TemporaryFolder()

    @Test
    fun closeQuietly_ignoresNullAndCloseExceptions() {
        var closed = false
        val closeable =
            Closeable {
                closed = true
                throw IllegalStateException("close failed")
            }

        IoUtils.closeQuietly(null as Closeable?)
        IoUtils.closeQuietly(closeable)

        assertThat(closed).isTrue()
    }

    @Test
    fun readOneLine_returnsRequestedLineOrNull() {
        val input = ByteArrayInputStream("one\ntwo\nthree".toByteArray(Charsets.UTF_8))

        assertThat(IoUtils.readOneLine(input, 2)).isEqualTo("two")

        val shortInput = ByteArrayInputStream("one".toByteArray(Charsets.UTF_8))
        assertThat(IoUtils.readOneLine(shortInput, 2)).isNull()
    }

    @Test
    fun saveReadAndCopyFile_preserveContent() {
        val source = temporaryFolder.newFile("source.txt")
        val target = File(temporaryFolder.root, "nested/target.txt")

        IoUtils.saveAsFile("hello\nworld", source.absolutePath)
        IoUtils.createParentDirsIfNeeded(target)
        IoUtils.copyFile(source.absolutePath, target.absolutePath)

        assertThat(IoUtils.readAllLines(target.absolutePath)).isEqualTo("hello\nworld")
    }

    @Test
    fun saveAsFile_inputStreamDoesNotCloseInput() {
        val target = temporaryFolder.newFile("stream.txt")
        val input =
            object : ByteArrayInputStream("stream-content".toByteArray(Charsets.UTF_8)) {
                var closed = false

                override fun close() {
                    closed = true
                    super.close()
                }
            }

        IoUtils.saveAsFile(input, target.absolutePath)

        assertThat(input.closed).isFalse()
        assertThat(IoUtils.readAllLines(target.absolutePath)).isEqualTo("stream-content")
    }
}
