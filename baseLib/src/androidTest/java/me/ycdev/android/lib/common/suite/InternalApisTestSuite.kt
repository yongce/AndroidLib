package me.ycdev.android.lib.common.suite

import me.ycdev.android.lib.common.internalapi.android.app.ActivityManagerIATest
import me.ycdev.android.lib.common.internalapi.android.os.PowerManagerIATest
import me.ycdev.android.lib.common.internalapi.android.os.ProcessIATest
import me.ycdev.android.lib.common.internalapi.android.os.ServiceManagerIATest
import me.ycdev.android.lib.common.internalapi.android.os.SystemPropertiesIATest
import me.ycdev.android.lib.common.internalapi.android.os.UserHandleIATest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    ActivityManagerIATest::class,
    PowerManagerIATest::class,
    ProcessIATest::class,
    ServiceManagerIATest::class,
    SystemPropertiesIATest::class,
    UserHandleIATest::class
)
class InternalApisTestSuite
