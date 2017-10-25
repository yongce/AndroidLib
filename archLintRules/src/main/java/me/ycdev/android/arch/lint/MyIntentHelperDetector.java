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

public class MyIntentHelperDetector extends WrapperDetectorBase {
    static final Issue ISSUE = Issue.create(
            "MyIntentHelper",
            "IntentHelper should be used.",
            "Please use the wrapper class 'IntentHelper' to get Intent extras"
                    + " to avoid security issues.",
            Category.CORRECTNESS, 5, Severity.ERROR,
            new Implementation(MyIntentHelperDetector.class, Scope.JAVA_FILE_SCOPE));

    @Override
    protected String getWrapperClassName() {
        return "me.ycdev.android.lib.common.wrapper.IntentHelper";
    }

    @Override
    protected String[] getTargetClassNames() {
        return new String[] {
                "android.content.Intent"
        };
    }

    @Override
    public List<String> getApplicableMethodNames() {
        return Arrays.asList(
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
        );
    }

    @Override
    protected void reportViolation(JavaContext context, UElement element) {
        context.report(ISSUE, element, context.getLocation(element),
                "Please use the wrapper class 'IntentHelper'.");
    }

}
