package me.ycdev.android.lib.common.utils

import com.google.common.truth.Truth.assertThat
import java.io.ByteArrayInputStream
import org.junit.Assert.assertThrows
import org.junit.Test

class DigestUtilsTest {
    @Test
    fun md5_hashesTextBytesAndStreamsConsistently() {
        val data = "AndroidLib".toByteArray(Charsets.UTF_8)

        assertThat(DigestUtils.md5("AndroidLib")).isEqualTo("2635b5535514d3a99070cb85baa503aa")
        assertThat(DigestUtils.md5(data)).isEqualTo("2635b5535514d3a99070cb85baa503aa")
        assertThat(DigestUtils.md5(ByteArrayInputStream(data)))
            .isEqualTo("2635b5535514d3a99070cb85baa503aa")
    }

    @Test
    fun sha1_hashesTextAndBytesConsistently() {
        val data = "AndroidLib".toByteArray(Charsets.UTF_8)

        assertThat(DigestUtils.sha1("AndroidLib"))
            .isEqualTo("544a2660ce0193317638d4b942e038dae5a8aca4")
        assertThat(DigestUtils.sha1(data))
            .isEqualTo("544a2660ce0193317638d4b942e038dae5a8aca4")
    }

    @Test
    fun md5_rejectsNullStream() {
        val e =
            assertThrows(IllegalArgumentException::class.java) {
                DigestUtils.md5(null)
            }

        assertThat(e).hasMessageThat().isEqualTo("Invalid input stream!")
    }
}
