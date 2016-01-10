package me.ycdev.android.arch.lint;

import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Issue;

import java.util.Collections;
import java.util.List;

public class MyIntentHelperDetectorTest extends AbstractCheckTest {
    @Override
    protected Detector getDetector() {
        return new MyIntentHelperDetector();
    }

    @Override
    protected List<Issue> getIssues() {
        return Collections.singletonList(MyIntentHelperDetector.ISSUE);
    }

    public void test() throws Exception {
        String result = lintProject("java/me/ycdev/android/arch/demo/wrapper/IntentHelperLintCase.java" +
                "=>src/me/ycdev/android/arch/demo/wrapper/IntentHelperLintCase.java");
        // TODO lint test engine cannot resolve their-party code (including Android Support Library)
        assertEquals("No warnings." +
                "", result);
    }
}
