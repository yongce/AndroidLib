package me.ycdev.android.arch.lint;

import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Issue;

import java.util.Collections;
import java.util.List;

public class MyBroadcastHelperDetectorTest extends AbstractCheckTest {
    @Override
    protected Detector getDetector() {
        return new MyBroadcastHelperDetector();
    }

    @Override
    protected List<Issue> getIssues() {
        return Collections.singletonList(MyBroadcastHelperDetector.ISSUE);
    }

    public void test() throws Exception {
        String result = lintProject("java/me/ycdev/android/arch/demo/wrapper/BroadcastHelperLintCase.java" +
                "=>src/me/ycdev/android/arch/demo/wrapper/BroadcastHelperLintCase.java");
        assertEquals("src/me/ycdev/android/arch/demo/wrapper/BroadcastHelperLintCase.java:28: Error: Please use the wrapper class 'BroadcastHelper'. [MyBroadcastHelper]\n" +
                "        return cxt.registerReceiver(receiver, filter); // lint violation\n" +
                "               ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                "src/me/ycdev/android/arch/demo/wrapper/BroadcastHelperLintCase.java:32: Error: Please use the wrapper class 'BroadcastHelper'. [MyBroadcastHelper]\n" +
                "        return cxt.registerReceiver(receiver, filter, null, null); // lint violation\n" +
                "               ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                "src/me/ycdev/android/arch/demo/wrapper/BroadcastHelperLintCase.java:36: Error: Please use the wrapper class 'BroadcastHelper'. [MyBroadcastHelper]\n" +
                "        cxt.sendBroadcast(intent, perm); // lint violation\n" +
                "        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                "src/me/ycdev/android/arch/demo/wrapper/BroadcastHelperLintCase.java:40: Error: Please use the wrapper class 'BroadcastHelper'. [MyBroadcastHelper]\n" +
                "        cxt.sendBroadcast(intent); // lint violation\n" +
                "        ~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                "4 errors, 0 warnings\n" +
                "", result);
    }

    public void testActivity() throws Exception {
        String result = lintProject("java/me/ycdev/android/arch/demo/activity/LintViolationActivity.java" +
                "=>src/me/ycdev/android/arch/demo/activity/LintViolationActivity.java");
        // TODO lint test engine cannot resolve their-party code (including Android Support Library)
        assertEquals("No warnings." +
                "", result);
    }
}
