package me.ycdev.android.arch.lint;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import lombok.ast.Node;
import me.ycdev.android.arch.lint.base.InheritDetectorBase;

public class MyBaseActivityDetector extends InheritDetectorBase {
    public static final Issue ISSUE = Issue.create(
            "MyBaseActivity",
            "Base classes for Activity should be used.",
            "Please use the base classes for Activity."
                    + " So that we can do some unified behaviors.",
            Category.CORRECTNESS, 5, Severity.ERROR,
            new Implementation(MyBaseActivityDetector.class, Scope.JAVA_FILE_SCOPE));

    @Override
    protected HashSet<String> getWrapperClasses() {
        HashSet<String> sets = new HashSet<>();
        sets.add("me.ycdev.android.arch.activity.BaseActivity");
        sets.add("me.ycdev.android.arch.activity.PreferenceBaseActivity");
        sets.add("me.ycdev.android.arch.activity.AppCompatBaseActivity");
        return sets;
    }

    @Override
    public List<String> applicableSuperClasses() {
        return Collections.singletonList("android.app.Activity");
    }

    @Override
    protected void reportViolation(JavaContext context, Node node) {
        context.report(ISSUE, node, context.getLocation(node),
                "Please use the base classes for Activity.");
    }
}
