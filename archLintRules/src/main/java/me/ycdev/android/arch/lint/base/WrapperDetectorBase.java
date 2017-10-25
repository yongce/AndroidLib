package me.ycdev.android.arch.lint.base;

import com.android.tools.lint.client.api.JavaEvaluator;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.JavaContext;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import org.jetbrains.uast.UCallExpression;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.UastUtils;

import java.util.List;

public abstract class WrapperDetectorBase extends Detector implements Detector.UastScanner {
    private String mWrapperClassName;
    private String[] mTargetClassNames;

    /** Constructs a new {@link WrapperDetectorBase} check */
    public WrapperDetectorBase() {
        mWrapperClassName = getWrapperClassName();
        mTargetClassNames = getTargetClassNames();
    }

    protected abstract String getWrapperClassName();

    protected abstract String[] getTargetClassNames();

    protected abstract void reportViolation(JavaContext context, UElement element);

    @Override
    public abstract List<String> getApplicableMethodNames();

    @Override
    public void visitMethod(JavaContext context, UCallExpression call, PsiMethod method) {
        JavaEvaluator evaluator = context.getEvaluator();
        PsiClass surroundingClass = UastUtils.getContainingClass(call);
        if (surroundingClass == null) {
            System.out.println("Fatal error in WrapperDetectorBase! Failed to get surrounding" +
                    " class \'" + call.getUastParent() + "\'");
            return;
        }

        String containingClassName = surroundingClass.getQualifiedName();
        if (mWrapperClassName.equals(containingClassName)) {
            return;
        }

        for (String targetClassName : mTargetClassNames) {
            if (evaluator.isMemberInSubClassOf(method, targetClassName, false)) {
                reportViolation(context, call);
                return;
            }
        }
    }
}
