package me.ycdev.android.arch.lint;

import com.android.tools.lint.checks.infrastructure.TestFile;
import com.android.tools.lint.checks.infrastructure.TestFiles;

import org.junit.Test;

import me.ycdev.android.arch.lint.utils.TestFileStubs;

import static com.android.tools.lint.checks.infrastructure.TestLintTask.lint;

public class MyBroadcastHelperDetectorTest {
    @Test
    public void testBroadcastHelperLintCase() throws Exception {
        TestFile testFile = TestFiles.java("" +
                "package me.ycdev.android.arch.demo.wrapper;\n" +
                "\n" +
                "import android.content.BroadcastReceiver;\n" +
                "import android.content.Context;\n" +
                "import android.content.Intent;\n" +
                "import android.content.IntentFilter;\n" +
                "\n" +
                "import me.ycdev.android.lib.common.wrapper.BroadcastHelper;\n" +
                "\n" +
                "public class BroadcastHelperLintCase {\n" +
                "    private static class Foo {\n" +
                "        public void registerReceiver() { // lint good\n" +
                "        }\n" +
                "\n" +
                "        public void sendBroadcast() { // lint good\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    public static void registerReceiver() { // lint good\n" +
                "        new Foo().registerReceiver();\n" +
                "    }\n" +
                "\n" +
                "    public static void sendBroadcast() { // lint good\n" +
                "        new Foo().sendBroadcast();\n" +
                "    }\n" +
                "\n" +
                "    public static Intent registerGood(Context cxt, BroadcastReceiver receiver, IntentFilter filter) {\n" +
                "        return BroadcastHelper.registerForInternal(cxt, receiver, filter); // lint good\n" +
                "    }\n" +
                "\n" +
                "    public static void sendToInternalGood(Context cxt, Intent intent) {\n" +
                "        BroadcastHelper.sendToInternal(cxt, intent); // lint good\n" +
                "    }\n" +
                "\n" +
                "    public static void sendToExternalGood(Context cxt, Intent intent, String perm) {\n" +
                "        BroadcastHelper.sendToExternal(cxt, intent, perm); // lint good\n" +
                "    }\n" +
                "\n" +
                "    public static void sendToExternal(Context cxt, Intent intent) {\n" +
                "        BroadcastHelper.sendToExternal(cxt, intent); // lint good\n" +
                "    }\n" +
                "\n" +
                "    public static Intent registerViolation(Context cxt, BroadcastReceiver receiver, IntentFilter filter) {\n" +
                "        return cxt.registerReceiver(receiver, filter); // lint violation\n" +
                "    }\n" +
                "\n" +
                "    public static Intent registerViolation2(Context cxt, BroadcastReceiver receiver, IntentFilter filter) {\n" +
                "        return cxt.registerReceiver(receiver, filter, null, null); // lint violation\n" +
                "    }\n" +
                "\n" +
                "    public static void sendViolation(Context cxt, Intent intent, String perm) {\n" +
                "        cxt.sendBroadcast(intent, perm); // lint violation\n" +
                "    }\n" +
                "\n" +
                "    public static void sendViolation2(Context cxt, Intent intent) {\n" +
                "        cxt.sendBroadcast(intent); // lint violation\n" +
                "    }\n" +
                "}\n");
        lint().files(TestFileStubs.getNonNull(), TestFileStubs.getBroadcastHelper(), testFile)
                .issues(MyBroadcastHelperDetector.ISSUE)
                .run()
                .expect("src/me/ycdev/android/arch/demo/wrapper/BroadcastHelperLintCase.java:44: Error: Please use the wrapper class 'BroadcastHelper'. [MyBroadcastHelper]\n" +
                        "        return cxt.registerReceiver(receiver, filter); // lint violation\n" +
                        "               ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                        "src/me/ycdev/android/arch/demo/wrapper/BroadcastHelperLintCase.java:48: Error: Please use the wrapper class 'BroadcastHelper'. [MyBroadcastHelper]\n" +
                        "        return cxt.registerReceiver(receiver, filter, null, null); // lint violation\n" +
                        "               ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                        "src/me/ycdev/android/arch/demo/wrapper/BroadcastHelperLintCase.java:52: Error: Please use the wrapper class 'BroadcastHelper'. [MyBroadcastHelper]\n" +
                        "        cxt.sendBroadcast(intent, perm); // lint violation\n" +
                        "        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                        "src/me/ycdev/android/arch/demo/wrapper/BroadcastHelperLintCase.java:56: Error: Please use the wrapper class 'BroadcastHelper'. [MyBroadcastHelper]\n" +
                        "        cxt.sendBroadcast(intent); // lint violation\n" +
                        "        ~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                        "4 errors, 0 warnings\n");
    }

    @Test
    public void testLintViolationActivity() throws Exception {
        TestFile testFile = TestFiles.java("" +
                "package me.ycdev.android.arch.demo.activity;\n" +
                "\n" +
                "import android.content.BroadcastReceiver;\n" +
                "import android.content.Context;\n" +
                "import android.content.Intent;\n" +
                "import android.content.IntentFilter;\n" +
                "import android.os.Bundle;\n" +
                "import android.support.v7.app.AppCompatActivity;\n" +
                "import android.view.MenuItem;\n" +
                "\n" +
                "\n" +
                "/**\n" +
                " * Class doc for test\n" +
                " */\n" +
                "public class LintViolationActivity extends AppCompatActivity { // lint violation\n" +
                "    private static final String TEST_ACTION = \"action.test\";\n" +
                "\n" +
                "    private BroadcastReceiver mReceiver = new BroadcastReceiver() {\n" +
                "        @Override\n" +
                "        public void onReceive(Context context, Intent intent) {\n" +
                "            // nothing to do\n" +
                "        }\n" +
                "    };\n" +
                "\n" +
                "    @Override\n" +
                "    protected void onCreate(Bundle savedInstanceState) {\n" +
                "        super.onCreate(savedInstanceState);\n" +
                "\n" +
                "\n" +
                "        IntentFilter filter = new IntentFilter();\n" +
                "        filter.addAction(TEST_ACTION);\n" +
                "        registerReceiver(mReceiver, filter); // lint violation\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public boolean onOptionsItemSelected(MenuItem item) {\n" +
                "        // Handle action bar item clicks here. The action bar will\n" +
                "        // automatically handle clicks on the Home/Up button, so long\n" +
                "        // as you specify a parent activity in AndroidManifest.xml.\n" +
                "        int id = item.getItemId();\n" +
                "\n" +
                "        sendBroadcast(new Intent(TEST_ACTION)); // lint violation\n" +
                "\n" +
                "        return super.onOptionsItemSelected(item);\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    protected void onDestroy() {\n" +
                "        super.onDestroy();\n" +
                "        unregisterReceiver(mReceiver);\n" +
                "    }\n" +
                "}\n");
        lint().files(TestFileStubs.getAppCompatActivity(), testFile)
                .issues(MyBroadcastHelperDetector.ISSUE)
                .run()
                .expect("src/me/ycdev/android/arch/demo/activity/LintViolationActivity.java:32: Error: Please use the wrapper class 'BroadcastHelper'. [MyBroadcastHelper]\n" +
                        "        registerReceiver(mReceiver, filter); // lint violation\n" +
                        "        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                        "src/me/ycdev/android/arch/demo/activity/LintViolationActivity.java:42: Error: Please use the wrapper class 'BroadcastHelper'. [MyBroadcastHelper]\n" +
                        "        sendBroadcast(new Intent(TEST_ACTION)); // lint violation\n" +
                        "        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                        "2 errors, 0 warnings\n");
    }
}
