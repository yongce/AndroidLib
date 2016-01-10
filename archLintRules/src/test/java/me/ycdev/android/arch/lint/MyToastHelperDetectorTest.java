package me.ycdev.android.arch.lint;

import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Issue;

import java.util.Collections;
import java.util.List;

public class MyToastHelperDetectorTest extends AbstractCheckTest {
    @Override
    protected Detector getDetector() {
        return new MyToastHelperDetector();
    }

    @Override
    protected List<Issue> getIssues() {
        return Collections.singletonList(MyToastHelperDetector.ISSUE);
    }

    public void test() throws Exception {
        String result = lintProject("java/me/ycdev/android/arch/demo/wrapper/ToastHelperLintCase.java" +
                "=>src/me/ycdev/android/arch/demo/wrapper/ToastHelperLintCase.java");
        assertEquals("src/me/ycdev/android/arch/demo/wrapper/ToastHelperLintCase.java:18: Error: Please use the wrapper class 'ToastHelper'. [MyToastHelper]\n" +
                "        Toast.makeText(cxt, msgResId, duration).show(); // lint violation\n" +
                "        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                "src/me/ycdev/android/arch/demo/wrapper/ToastHelperLintCase.java:22: Error: Please use the wrapper class 'ToastHelper'. [MyToastHelper]\n" +
                "        Toast.makeText(cxt, msg, duration).show(); // lint violation\n" +
                "        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                "2 errors, 0 warnings\n" +
                        "", result);
    }
}
