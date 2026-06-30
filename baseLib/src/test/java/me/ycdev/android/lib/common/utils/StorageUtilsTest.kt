package me.ycdev.android.lib.common.utils

import android.os.Environment
import com.google.common.truth.Truth.assertThat
import java.nio.file.Files
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowEnvironment

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class StorageUtilsTest {
    @After
    fun tearDown() {
        ShadowEnvironment.reset()
    }

    @Test
    fun externalStorageAvailabilityReflectsEnvironmentState() {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED)
        assertThat(StorageUtils.isExternalStorageAvailable()).isTrue()

        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_UNMOUNTED)
        assertThat(StorageUtils.isExternalStorageAvailable()).isFalse()
    }

    @Test
    fun externalStoragePathReflectsConfiguredDirectory() {
        val storageDir = Files.createTempDirectory("androidlib-storage").toAbsolutePath()
        ShadowEnvironment.setExternalStorageDirectory(storageDir)

        assertThat(StorageUtils.getExternalStoragePath()).isEqualTo(storageDir.toFile().absolutePath)
    }

    @Test
    fun externalStorageRemovableAndEmulatedReflectShadowState() {
        ShadowEnvironment.setIsExternalStorageEmulated(true)

        assertThat(StorageUtils.isExternalStorageEmulated()).isTrue()
    }

    @Test
    fun spaceHelpersReturnZeroForMissingPath() {
        val missingPath = RuntimeEnvironment.getApplication().cacheDir.resolve("missing-space-path")

        assertThat(StorageUtils.getFreeSpace(missingPath)).isEqualTo(0)
        assertThat(StorageUtils.getTotalSpace(missingPath)).isEqualTo(0)
    }

    @Test
    fun getExternalCacheDirDelegatesToContext() {
        val context = RuntimeEnvironment.getApplication()

        assertThat(StorageUtils.getExternalCacheDir(context)).isEqualTo(context.externalCacheDir)
    }
}
