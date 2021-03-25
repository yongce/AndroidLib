package me.ycdev.android.arch.lint.base

import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.JavaContext
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import java.util.HashSet

abstract class InheritDetectorBase : Detector(), Detector.UastScanner {
    protected abstract val applicableClasses: List<String>
    protected abstract val wrapperClasses: HashSet<String>
    protected abstract fun reportViolation(context: JavaContext, element: UElement)

    override fun applicableSuperClasses(): List<String>? = applicableClasses

    override fun visitClass(context: JavaContext, declaration: UClass) {
        val wrappers = wrapperClasses
        val className = declaration.qualifiedName
        if (wrappers.contains(className)) {
            return // ignore the wrapper classes
        }

        val evaluator = context.evaluator
        var found = false
        for (wrapperClass in wrappers) {
            if (evaluator.inheritsFrom(declaration, wrapperClass, false)) {
                found = true
                break
            }
        }
        if (!found) {
            reportViolation(context, declaration)
        }
    }
}
