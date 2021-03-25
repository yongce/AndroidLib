package me.ycdev.android.arch.lint

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import me.ycdev.android.arch.lint.base.InheritDetectorBase
import org.jetbrains.uast.UElement
import java.util.HashSet

class MyBaseActivityDetector : InheritDetectorBase() {
    override val applicableClasses: List<String> = arrayListOf("android.app.Activity")

    override val wrapperClasses: HashSet<String> = hashSetOf(
        "me.ycdev.android.arch.activity.BaseActivity",
        "me.ycdev.android.arch.activity.PreferenceBaseActivity",
        "me.ycdev.android.arch.activity.AppCompatBaseActivity"
    )

    override fun reportViolation(context: JavaContext, element: UElement) {
        context.report(
            ISSUE, element, context.getNameLocation(element),
            "Please use the base classes for Activity."
        )
    }

    companion object {
        internal val ISSUE = Issue.create(
            "MyBaseActivity",
            "Base classes for Activity should be used.",
            "Please use the base classes for Activity." + " So that we can do some unified behaviors.",
            Category.CORRECTNESS, 5, Severity.ERROR,
            Implementation(MyBaseActivityDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
