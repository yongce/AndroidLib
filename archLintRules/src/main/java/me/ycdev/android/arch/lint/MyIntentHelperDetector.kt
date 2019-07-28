package me.ycdev.android.arch.lint

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import me.ycdev.android.arch.lint.base.WrapperDetectorBase
import org.jetbrains.uast.UElement

class MyIntentHelperDetector : WrapperDetectorBase() {
    override val applicableMethods = arrayListOf(
        "hasExtra",
        "getBooleanArrayExtra",
        "getBooleanExtra",
        "getBundleExtra",
        "getByteArrayExtra",
        "getByteExtra",
        "getCharArrayExtra",
        "getCharExtra",
        "getCharSequenceArrayExtra",
        "getCharSequenceArrayListExtra",
        "getCharSequenceExtra",
        "getDoubleArrayExtra",
        "getDoubleExtra",
        "getExtra",
        "getExtras",
        "getFloatArrayExtra",
        "getFloatExtra",
        "getIBinderExtra",
        "getIntArrayExtra",
        "getIBinderExtra",
        "getIntArrayExtra",
        "getIntegerArrayListExtra",
        "getIntExtra",
        "getLongArrayExtra",
        "getLongExtra",
        "getParcelableArrayExtra",
        "getParcelableArrayListExtra",
        "getParcelableExtra",
        "getSerializableExtra",
        "getShortArrayExtra",
        "getShortExtra",
        "getStringArrayExtra",
        "getStringArrayListExtra",
        "getStringExtra"
    )

    override val wrapperClassName = "me.ycdev.android.lib.common.wrapper.IntentHelper"

    override val targetClassNames = arrayOf("android.content.Intent")

    override fun reportViolation(context: JavaContext, element: UElement) {
        context.report(
            ISSUE, element, context.getLocation(element),
            "Please use the wrapper class 'IntentHelper'."
        )
    }

    companion object {
        internal val ISSUE = Issue.create(
            "MyIntentHelper",
            "IntentHelper should be used.",
            "Please use the wrapper class 'IntentHelper' to get Intent extras" + " to avoid security issues.",
            Category.CORRECTNESS, 5, Severity.ERROR,
            Implementation(MyIntentHelperDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
