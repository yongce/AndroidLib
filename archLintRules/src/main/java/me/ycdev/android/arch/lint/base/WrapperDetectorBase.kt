package me.ycdev.android.arch.lint.base

import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.JavaContext
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.getContainingUClass

abstract class WrapperDetectorBase : Detector(), Detector.UastScanner {
    protected abstract val applicableMethods: List<String>
    protected abstract val wrapperClassName: String
    protected abstract val targetClassNames: Array<String>
    protected abstract fun reportViolation(context: JavaContext, element: UElement)

    override fun getApplicableMethodNames(): List<String> = applicableMethods

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        val evaluator = context.evaluator
        val surroundingClass = node.getContainingUClass()?.javaPsi
        if (surroundingClass == null) {
            println(
                "Fatal error in WrapperDetectorBase! Failed to get surrounding" +
                    " class \'" + node.uastParent + "\'"
            )
            return
        }

        val containingClassName = surroundingClass.qualifiedName
        if (wrapperClassName == containingClassName) {
            return
        }

        for (targetClassName in targetClassNames) {
            if (evaluator.isMemberInSubClassOf(method, targetClassName, false)) {
                reportViolation(context, node)
                return
            }
        }
    }
}
