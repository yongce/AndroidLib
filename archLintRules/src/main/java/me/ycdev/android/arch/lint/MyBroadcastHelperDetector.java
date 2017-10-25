package me.ycdev.android.arch.lint;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;

import org.jetbrains.uast.UElement;

import java.util.Arrays;
import java.util.List;

import me.ycdev.android.arch.lint.base.WrapperDetectorBase;

public class MyBroadcastHelperDetector extends WrapperDetectorBase {
    static final Issue ISSUE = Issue.create(
            "MyBroadcastHelper",
            "BroadcastHelper should be used.",
            "Please use the wrapper class 'BroadcastHelper' to register broadcast receivers"
                    + " and send broadcasts to avoid security issues.",
            Category.CORRECTNESS, 5, Severity.ERROR,
            new Implementation(MyBroadcastHelperDetector.class, Scope.JAVA_FILE_SCOPE));

    @Override
    protected String getWrapperClassName() {
        return "me.ycdev.android.lib.common.wrapper.BroadcastHelper";
    }

    @Override
    protected String[] getTargetClassNames() {
        return new String[] {
                "android.content.Context"
        };
    }

    @Override
    public List<String> getApplicableMethodNames() {
        return Arrays.asList(
                "registerReceiver",
                "sendBroadcast");
    }

    @Override
    protected void reportViolation(JavaContext context, UElement element) {
        context.report(ISSUE, element, context.getLocation(element),
                "Please use the wrapper class 'BroadcastHelper'.");
    }
}
