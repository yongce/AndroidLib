package me.ycdev.android.arch.lint.base;

import com.android.annotations.NonNull;
import com.android.annotations.Nullable;
import com.android.tools.lint.client.api.JavaParser;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Speed;

import java.util.HashSet;
import java.util.List;

import lombok.ast.ClassDeclaration;
import lombok.ast.Node;

public abstract class InheritDetectorBase extends Detector implements Detector.JavaScanner {
    protected HashSet<String> mWrapperClasses;
    protected HashSet<String> mTargetClasses;

    /** Constructs a new {@link InheritDetectorBase} check */
    public InheritDetectorBase() {
        mWrapperClasses = getWrapperClasses();
        mTargetClasses = new HashSet<>(applicableSuperClasses());
    }

    protected abstract HashSet<String> getWrapperClasses();

    @Override
    public abstract List<String> applicableSuperClasses();

    protected abstract void reportViolation(JavaContext context, Node node);

    @Override
    public void checkClass(@NonNull JavaContext context, @Nullable ClassDeclaration declaration,
            @NonNull Node node, @NonNull JavaParser.ResolvedClass resolvedClass) {
        String className = resolvedClass.getName();
        if (mWrapperClasses.contains(className)) {
            return; // ignore the wrapper classes
        }

        JavaParser.ResolvedClass superClass = resolvedClass.getSuperClass();
        String superClassName = superClass.getName();
        while (!mWrapperClasses.contains(superClassName)) {
            if (mTargetClasses.contains(superClassName)) {
                Node locationNode = node instanceof ClassDeclaration
                        ? ((ClassDeclaration) node).astName() : node;
                reportViolation(context, locationNode);
                break;
            }
            superClass = superClass.getSuperClass();
            superClassName = superClass.getName();
        }
    }

    @Override
    public Speed getSpeed() {
        return Speed.FAST;
    }
}
