package me.ycdev.android.arch.lint

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import me.ycdev.android.arch.lint.base.WrapperDetectorBase
import org.jetbrains.uast.UElement

class MyBroadcastHelperDetector : WrapperDetectorBase() {
    override val applicableMethods = arrayListOf("registerReceiver", "sendBroadcast")

    override val wrapperClassName = "me.ycdev.android.lib.common.wrapper.BroadcastHelper"

    override val targetClassNames = arrayOf("android.content.Context")

    override fun reportViolation(context: JavaContext, element: UElement) {
        context.report(
            ISSUE, element, context.getLocation(element),
            "Please use the wrapper class 'BroadcastHelper'."
        )
    }

    companion object {
        internal val ISSUE = Issue.create(
            "MyBroadcastHelper",
            "BroadcastHelper should be used.",
            "Please use the wrapper class 'BroadcastHelper' to register broadcast receivers" +
                    " and send broadcasts to avoid security issues.",
            Category.CORRECTNESS, 5, Severity.ERROR,
            Implementation(MyBroadcastHelperDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
