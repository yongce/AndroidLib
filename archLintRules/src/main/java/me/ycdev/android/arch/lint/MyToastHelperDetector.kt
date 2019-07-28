package me.ycdev.android.arch.lint

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import me.ycdev.android.arch.lint.base.WrapperDetectorBase
import org.jetbrains.uast.UElement

class MyToastHelperDetector : WrapperDetectorBase() {
    override val applicableMethods = arrayListOf("makeText")

    override val wrapperClassName = "me.ycdev.android.arch.wrapper.ToastHelper"

    override val targetClassNames = arrayOf("android.widget.Toast")

    override fun reportViolation(context: JavaContext, element: UElement) {
        context.report(
            ISSUE, element, context.getLocation(element),
            "Please use the wrapper class 'ToastHelper'."
        )
    }

    companion object {
        internal val ISSUE = Issue.create(
            "MyToastHelper",
            "ToastHelper should be used.",
            "Please use the wrapper class 'ToastHelper' to show toast." + " So that we can customize and unify the UI in future.",
            Category.CORRECTNESS, 5, Severity.ERROR,
            Implementation(MyToastHelperDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
