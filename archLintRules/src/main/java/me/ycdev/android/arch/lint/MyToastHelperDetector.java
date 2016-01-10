package me.ycdev.android.arch.lint;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.ast.MethodInvocation;
import me.ycdev.android.arch.lint.base.WrapperDetectorBase;

public class MyToastHelperDetector extends WrapperDetectorBase {
    public static final Issue ISSUE = Issue.create(
            "MyToastHelper",
            "ToastHelper should be used.",
            "Please use the wrapper class 'ToastHelper' to show toast."
                    + " So that we can customize and unify the UI in future.",
            Category.CORRECTNESS, 5, Severity.ERROR,
            new Implementation(MyToastHelperDetector.class, Scope.JAVA_FILE_SCOPE));

    /** Constructs a new {@link MyToastHelperDetector} check */
    public MyToastHelperDetector() {
        super();
    }

    @Override
    protected String getWrapperClassName() {
        return "me.ycdev.android.arch.wrapper.ToastHelper";
    }

    @Override
    protected String[] getTargetClassNames() {
        return new String[] {
                "android.widget.Toast"
        };
    }

    @Override
    public List<String> getApplicableMethodNames() {
        return Collections.singletonList("makeText");
    }

    @Override
    protected void reportViolation(JavaContext context, MethodInvocation node) {
        context.report(ISSUE, node, context.getLocation(node),
                "Please use the wrapper class 'ToastHelper'.");
    }
}
