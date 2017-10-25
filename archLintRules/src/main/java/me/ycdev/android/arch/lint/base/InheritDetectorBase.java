package me.ycdev.android.arch.lint.base;

import com.android.tools.lint.client.api.JavaEvaluator;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.JavaContext;

import org.jetbrains.uast.UClass;
import org.jetbrains.uast.UElement;

import java.util.HashSet;
import java.util.List;

public abstract class InheritDetectorBase extends Detector implements Detector.UastScanner {
    private HashSet<String> mWrapperClasses;

    /** Constructs a new {@link InheritDetectorBase} check */
    public InheritDetectorBase() {
        mWrapperClasses = getWrapperClasses();
    }

    protected abstract HashSet<String> getWrapperClasses();

    @Override
    public abstract List<String> applicableSuperClasses();

    protected abstract void reportViolation(JavaContext context, UElement element);

    @Override
    public void visitClass(JavaContext context, UClass cls) {
        String className = cls.getQualifiedName();
        if (mWrapperClasses.contains(className)) {
            return; // ignore the wrapper classes
        }

        JavaEvaluator evaluator = context.getEvaluator();
        boolean found = false;
        for (String wrapperClass : mWrapperClasses) {
            if (evaluator.inheritsFrom(cls, wrapperClass, false)) {
                found = true;
                break;
            }
        }
        if (!found) {
            reportViolation(context, cls);
        }
    }
}
