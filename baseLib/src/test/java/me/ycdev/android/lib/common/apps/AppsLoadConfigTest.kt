package me.ycdev.android.lib.common.apps

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AppsLoadConfigTest {
    @Test
    fun configDefaults_loadLabelAndIcon() {
        val config = AppsLoadConfig()

        assertThat(config.loadLabel).isTrue()
        assertThat(config.loadIcon).isTrue()
    }

    @Test
    fun configAllowsDisablingHeavyFields() {
        val config = AppsLoadConfig(loadLabel = false, loadIcon = false)

        assertThat(config.loadLabel).isFalse()
        assertThat(config.loadIcon).isFalse()
    }

    @Test
    fun filterDefaults_keepMountedEnabledAndAllAppTypes() {
        val filter = AppsLoadFilter()

        assertThat(filter.onlyMounted).isTrue()
        assertThat(filter.onlyEnabled).isTrue()
        assertThat(filter.includeSysApp).isTrue()
        assertThat(filter.includeUpdatedSysApp).isTrue()
        assertThat(filter.includeMyself).isTrue()
    }

    @Test
    fun filterFlagsCanBeCombined() {
        val filter = AppsLoadFilter().apply {
            onlyMounted = false
            onlyEnabled = false
            includeSysApp = false
            includeUpdatedSysApp = true
            includeMyself = false
        }

        assertThat(filter.onlyMounted).isFalse()
        assertThat(filter.onlyEnabled).isFalse()
        assertThat(filter.includeSysApp).isFalse()
        assertThat(filter.includeUpdatedSysApp).isTrue()
        assertThat(filter.includeMyself).isFalse()
    }
}
