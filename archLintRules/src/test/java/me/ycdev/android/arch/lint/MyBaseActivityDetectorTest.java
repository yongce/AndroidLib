package me.ycdev.android.arch.lint;

import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Issue;

import java.util.Collections;
import java.util.List;

public class MyBaseActivityDetectorTest extends AbstractCheckTest {
    @Override
    protected Detector getDetector() {
        return new MyBaseActivityDetector();
    }

    @Override
    protected List<Issue> getIssues() {
        return Collections.singletonList(MyBaseActivityDetector.ISSUE);
    }

    public void testGood() throws Exception {
        String result = lintProject("java/me/ycdev/android/arch/demo/activity/LintGoodActivity.java" +
                "=>src/me/ycdev/android/arch/demo/activity/LintGoodActivity.java");
        assertEquals("No warnings." +
                "", result);
    }

    public void testGood2() throws Exception {
        String result = lintProject("java/me/ycdev/android/arch/demo/activity/LintGood2Activity.java" +
                "=>src/me/ycdev/android/arch/demo/activity/LintGood2Activity.java");
        assertEquals("No warnings." +
                "", result);
    }

    public void testGood3() throws Exception {
        String result = lintProject("java/me/ycdev/android/arch/demo/activity/LintGood3Activity.java" +
                "=>src/me/ycdev/android/arch/demo/activity/LintGood3Activity.java");
        assertEquals("No warnings." +
                "", result);
    }

    public void testViolation() throws Exception {
        String result = lintProject("java/me/ycdev/android/arch/demo/activity/LintViolationActivity.java" +
                "=>src/me/ycdev/android/arch/demo/activity/LintViolationActivity.java");
        // TODO lint test engine cannot resolve their-party code (including Android Support Library)
        assertEquals("No warnings." +
                "", result);
    }

    public void testViolation2() throws Exception {
        String result = lintProject("java/me/ycdev/android/arch/demo/activity/LintViolation2Activity.java" +
                "=>src/me/ycdev/android/arch/demo/activity/LintViolation2Activity.java");
        assertEquals("src/me/ycdev/android/arch/demo/activity/LintViolation2Activity.java:9: Error: Please use the base classes for Activity. [MyBaseActivity]\n" +
                "public class LintViolation2Activity extends Activity { // lint violation\n" +
                "             ~~~~~~~~~~~~~~~~~~~~~~\n" +
                "1 errors, 0 warnings\n" +
                "", result);
    }
}
