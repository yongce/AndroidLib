package me.ycdev.android.arch.lint.base;

import com.android.tools.lint.client.api.JavaParser;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.JavaContext;

import java.util.List;

import lombok.ast.AstVisitor;
import lombok.ast.ClassDeclaration;
import lombok.ast.MethodInvocation;

public abstract class WrapperDetectorBase extends Detector implements Detector.JavaScanner {
    protected String mWrapperClassName;
    protected String[] mTargetClassNames;

    /** Constructs a new {@link WrapperDetectorBase} check */
    public WrapperDetectorBase() {
        mWrapperClassName = getWrapperClassName();
        mTargetClassNames = getTargetClassNames();
    }

    // ---- Implements JavaScanner ----

    protected abstract String getWrapperClassName();

    protected abstract String[] getTargetClassNames();

    protected abstract void reportViolation(JavaContext context, MethodInvocation node);

    @Override
    public abstract List<String> getApplicableMethodNames();

    @Override
    public void visitMethod(JavaContext context, AstVisitor visitor, MethodInvocation node) {
        if (isInvokedInWrapperClass(context, node)) {
            return;
        }

        if (checkRuleViolation(context, node)) {
            reportViolation(context, node);
        }
    }

    private boolean isInvokedInWrapperClass(JavaContext context, MethodInvocation node) {
        ClassDeclaration surroundingClassDecl = JavaContext.findSurroundingClass(node);
        JavaParser.ResolvedClass surroundingClass = (JavaParser.ResolvedClass)
                context.resolve(surroundingClassDecl);
        String surroundingClassName = surroundingClass.getName();
        return mWrapperClassName.equals(surroundingClassName);
    }

    private boolean checkRuleViolation(JavaContext context, MethodInvocation node) {
        JavaParser.ResolvedMethod method = (JavaParser.ResolvedMethod) context.resolve(node);
        JavaParser.ResolvedClass containingClass = method.getContainingClass();
        String containingClassName = containingClass.getName();
        if (mWrapperClassName.equals(containingClassName)) {
            return false;
        }

        for (String targetClassName : mTargetClassNames) {
            if (targetClassName.equals(containingClassName)) {
                return true;
            }
            if (containingClass.isSubclassOf(targetClassName, false)) {
                return true;
            }
        }
        return false;
    }

}
