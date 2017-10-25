package me.ycdev.android.arch.lint;

import com.android.tools.lint.checks.infrastructure.TestFile;
import com.android.tools.lint.checks.infrastructure.TestFiles;

import org.junit.Test;

import me.ycdev.android.arch.lint.utils.TestFileStubs;

import static com.android.tools.lint.checks.infrastructure.TestLintTask.lint;

public class MyBaseActivityDetectorTest {
    @Test
    public void testLintGoodActivity() {
        TestFile testFile = TestFiles.java("" +
                "package me.ycdev.android.arch.demo.activity;\n" +
                "\n" +
                "import android.os.Bundle;\n" +
                "import android.view.Menu;\n" +
                "import android.view.MenuItem;\n" +
                "\n" +
                "import me.ycdev.android.arch.activity.AppCompatBaseActivity;\n" +
                "\n" +
                "\n" +
                "public class LintGoodActivity extends AppCompatBaseActivity { // lint good\n" +
                "\n" +
                "    @Override\n" +
                "    protected void onCreate(Bundle savedInstanceState) {\n" +
                "        super.onCreate(savedInstanceState);\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public boolean onCreateOptionsMenu(Menu menu) {\n" +
                "        // Inflate the menu; this adds items to the action bar if it is present.\n" +
                "        return true;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public boolean onOptionsItemSelected(MenuItem item) {\n" +
                "        // Handle action bar item clicks here. The action bar will\n" +
                "        // automatically handle clicks on the Home/Up button, so long\n" +
                "        // as you specify a parent activity in AndroidManifest.xml.\n" +
                "        int id = item.getItemId();\n" +
                "\n" +
                "        return super.onOptionsItemSelected(item);\n" +
                "    }\n" +
                "}\n");
        lint().files(TestFileStubs.getAppCompatBaseActivity(), testFile)
                .issues(MyBaseActivityDetector.ISSUE)
                .run()
                .expect("No warnings.");
    }

    @Test
    public void testLintGood2Activity() {
        TestFile testFile = TestFiles.java("" +
                "package me.ycdev.android.arch.demo.activity;\n" +
                "\n" +
                "import android.os.Bundle;\n" +
                "\n" +
                "import me.ycdev.android.arch.activity.BaseActivity;\n" +
                "\n" +
                "public class LintGood2Activity extends BaseActivity { // lint good\n" +
                "    @Override\n" +
                "    protected void onCreate(Bundle savedInstanceState) {\n" +
                "        super.onCreate(savedInstanceState);\n" +
                "    }\n" +
                "}\n");
        lint().files(TestFileStubs.getBaseActivity(), testFile)
                .issues(MyBaseActivityDetector.ISSUE)
                .run()
                .expect("No warnings.");
    }

    @Test
    public void testLintGood3Activity() {
        TestFile good2File = TestFiles.java("" +
                "package me.ycdev.android.arch.demo.activity;\n" +
                "\n" +
                "import android.os.Bundle;\n" +
                "\n" +
                "import me.ycdev.android.arch.activity.BaseActivity;\n" +
                "\n" +
                "public class LintGood2Activity extends BaseActivity { // lint good\n" +
                "    @Override\n" +
                "    protected void onCreate(Bundle savedInstanceState) {\n" +
                "        super.onCreate(savedInstanceState);\n" +
                "    }\n" +
                "}\n");
        TestFile good3File = TestFiles.java("" +
                "package me.ycdev.android.arch.demo.activity;\n" +
                "\n" +
                "public class LintGood3Activity extends LintGood2Activity { // lint good\n" +
                "    // nothing to do\n" +
                "}\n");
        lint().files(TestFileStubs.getBaseActivity(), good2File, good3File)
                .issues(MyBaseActivityDetector.ISSUE)
                .run()
                .expect("No warnings.");
    }

    @Test
    public void testLintViolationActivity() {
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
                .issues(MyBaseActivityDetector.ISSUE)
                .run()
                .expect("src/me/ycdev/android/arch/demo/activity/LintViolationActivity.java:15: Error: Please use the base classes for Activity. [MyBaseActivity]\n" +
                        "public class LintViolationActivity extends AppCompatActivity { // lint violation\n" +
                        "             ~~~~~~~~~~~~~~~~~~~~~\n" +
                        "1 errors, 0 warnings\n");
    }

    @Test
    public void testLintViolation2Activity() {
        TestFile testFile = TestFiles.java("" +
                "package me.ycdev.android.arch.demo.activity;\n" +
                "\n" +
                "import android.app.Activity;\n" +
                "import android.os.Bundle;\n" +
                "\n" +
                "// class comment for test\n" +
                "public class LintViolation2Activity extends Activity { // lint violation\n" +
                "    @Override\n" +
                "    protected void onCreate(Bundle savedInstanceState) {\n" +
                "        super.onCreate(savedInstanceState);\n" +
                "    }\n" +
                "}\n");
        lint().files(testFile)
                .issues(MyBaseActivityDetector.ISSUE)
                .run()
                .expect("src/me/ycdev/android/arch/demo/activity/LintViolation2Activity.java:7: Error: Please use the base classes for Activity. [MyBaseActivity]\n" +
                        "public class LintViolation2Activity extends Activity { // lint violation\n" +
                        "             ~~~~~~~~~~~~~~~~~~~~~~\n" +
                        "1 errors, 0 warnings\n");
    }
}
