package me.ycdev.android.arch.lint

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import me.ycdev.android.arch.lint.utils.TestFileStubs
import org.junit.Test

class MyBaseActivityDetectorTest {
    @Test
    fun testLintGoodActivity_java() {
        val testFile = TestFiles.java(
            "" +
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
                "}\n"
        )
        lint().files(TestFileStubs.appCompatBaseActivity, testFile)
            .issues(MyBaseActivityDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun testLintGoodActivity_kotlin() {
        val testFile = TestFiles.kotlin(
            "package me.ycdev.android.arch.demo.activity\n" +
                "\n" +
                "import android.os.Bundle\n" +
                "import android.view.Menu\n" +
                "import android.view.MenuItem\n" +
                "\n" +
                "import me.ycdev.android.arch.activity.AppCompatBaseActivity\n" +
                "\n" +
                "class LintGoodActivity : AppCompatBaseActivity() { // lint good\n" +
                "\n" +
                "    override fun onCreate(savedInstanceState: Bundle?) {\n" +
                "        super.onCreate(savedInstanceState)\n" +
                "    }\n" +
                "\n" +
                "    override fun onCreateOptionsMenu(menu: Menu): Boolean {\n" +
                "        // Inflate the menu; this adds items to the action bar if it is present.\n" +
                "        return true\n" +
                "    }\n" +
                "\n" +
                "    override fun onOptionsItemSelected(item: MenuItem): Boolean {\n" +
                "        // Handle action bar item clicks here. The action bar will\n" +
                "        // automatically handle clicks on the Home/Up button, so long\n" +
                "        // as you specify a parent activity in AndroidManifest.xml.\n" +
                "        val id = item.itemId\n" +
                "\n" +
                "        return super.onOptionsItemSelected(item)\n" +
                "    }\n" +
                "}\n"
        )
        lint().files(TestFileStubs.appCompatBaseActivity, testFile)
            .issues(MyBaseActivityDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun testLintGood2Activity_java() {
        val testFile = TestFiles.java(
            "" +
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
                "}\n"
        )
        lint().files(TestFileStubs.baseActivity, testFile)
            .issues(MyBaseActivityDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun testLintGood2Activity_kotlin() {
        val testFile = TestFiles.kotlin(
            "package me.ycdev.android.arch.demo.activity\n" +
                "\n" +
                "import android.os.Bundle\n" +
                "\n" +
                "import me.ycdev.android.arch.activity.BaseActivity\n" +
                "\n" +
                "open class LintGood2Activity : BaseActivity() { // lint good\n" +
                "    override fun onCreate(savedInstanceState: Bundle?) {\n" +
                "        super.onCreate(savedInstanceState)\n" +
                "    }\n" +
                "}\n"
        )
        lint().files(TestFileStubs.baseActivity, testFile)
            .issues(MyBaseActivityDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun testLintGood3Activity_java() {
        val good2File = TestFiles.java(
            "" +
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
                "}\n"
        )
        val good3File = TestFiles.java(
            "" +
                "package me.ycdev.android.arch.demo.activity;\n" +
                "\n" +
                "public class LintGood3Activity extends LintGood2Activity { // lint good\n" +
                "    // nothing to do\n" +
                "}\n"
        )
        lint().files(TestFileStubs.baseActivity, good2File, good3File)
            .issues(MyBaseActivityDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun testLintGood3Activity_kotlin() {
        val good2File = TestFiles.kotlin(
            "package me.ycdev.android.arch.demo.activity\n" +
                "\n" +
                "import android.os.Bundle\n" +
                "\n" +
                "import me.ycdev.android.arch.activity.BaseActivity\n" +
                "\n" +
                "open class LintGood2Activity : BaseActivity() { // lint good\n" +
                "    override fun onCreate(savedInstanceState: Bundle?) {\n" +
                "        super.onCreate(savedInstanceState)\n" +
                "    }\n" +
                "}\n"
        )
        val good3File = TestFiles.kotlin(
            "package me.ycdev.android.arch.demo.activity\n" +
                "\n" +
                "class LintGood3Activity : LintGood2Activity() // lint good\n" +
                "// nothing to do\n"
        )
        lint().files(TestFileStubs.baseActivity, good2File, good3File)
            .issues(MyBaseActivityDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun testLintViolationActivity_java() {
        val testFile = TestFiles.java(
            "" +
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
                "    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {\n" +
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
                "}\n"
        )
        lint().files(TestFileStubs.appCompatActivity, testFile)
            .issues(MyBaseActivityDetector.ISSUE)
            .run()
            .expect(
                "src/me/ycdev/android/arch/demo/activity/LintViolationActivity.java:15: Error: Please use the base classes for Activity. [MyBaseActivity]\n" +
                    "public class LintViolationActivity extends AppCompatActivity { // lint violation\n" +
                    "             ~~~~~~~~~~~~~~~~~~~~~\n" +
                    "1 errors, 0 warnings\n"
            )
    }

    @Test
    fun testLintViolationActivity_kotlin() {
        val testFile = TestFiles.kotlin(
            "package me.ycdev.android.arch.demo.activity\n" +
                "\n" +
                "import android.content.BroadcastReceiver\n" +
                "import android.content.Context\n" +
                "import android.content.Intent\n" +
                "import android.content.IntentFilter\n" +
                "import android.os.Bundle\n" +
                "import androidx.appcompat.app.AppCompatActivity\n" +
                "import android.view.MenuItem\n" +
                "\n" +
                "/**\n" +
                " * Class doc for test\n" +
                " */\n" +
                "class LintViolationActivity : AppCompatActivity() { // lint violation\n" +
                "\n" +
                "    private val receiver = object : BroadcastReceiver() {\n" +
                "        override fun onReceive(context: Context, intent: Intent) {\n" +
                "            // nothing to do\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    override fun onCreate(savedInstanceState: Bundle?) {\n" +
                "        super.onCreate(savedInstanceState)\n" +
                "\n" +
                "        val filter = IntentFilter()\n" +
                "        filter.addAction(TEST_ACTION)\n" +
                "        registerReceiver(receiver, filter) // lint violation\n" +
                "    }\n" +
                "\n" +
                "    override fun onOptionsItemSelected(item: MenuItem): Boolean {\n" +
                "        // Handle action bar item clicks here. The action bar will\n" +
                "        // automatically handle clicks on the Home/Up button, so long\n" +
                "        // as you specify a parent activity in AndroidManifest.xml.\n" +
                "        val id = item.itemId\n" +
                "\n" +
                "        sendBroadcast(Intent(TEST_ACTION)) // lint violation\n" +
                "\n" +
                "        return super.onOptionsItemSelected(item)\n" +
                "    }\n" +
                "\n" +
                "    override fun onDestroy() {\n" +
                "        super.onDestroy()\n" +
                "        unregisterReceiver(receiver)\n" +
                "    }\n" +
                "\n" +
                "    companion object {\n" +
                "        private const val TEST_ACTION = \"action.test\"\n" +
                "    }\n" +
                "}\n"
        )
        lint().files(TestFileStubs.appCompatActivityAndroidX, testFile)
            .issues(MyBaseActivityDetector.ISSUE)
            .run()
            .expect(
                "src/me/ycdev/android/arch/demo/activity/LintViolationActivity.kt:14: Error: Please use the base classes for Activity. [MyBaseActivity]\n" +
                    "class LintViolationActivity : AppCompatActivity() { // lint violation\n" +
                    "      ~~~~~~~~~~~~~~~~~~~~~\n" +
                    "1 errors, 0 warnings"
            )
    }

    @Test
    fun testLintViolation2Activity_java() {
        val testFile = TestFiles.java(
            "" +
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
                "}\n"
        )
        lint().files(testFile)
            .issues(MyBaseActivityDetector.ISSUE)
            .run()
            .expect(
                "src/me/ycdev/android/arch/demo/activity/LintViolation2Activity.java:7: Error: Please use the base classes for Activity. [MyBaseActivity]\n" +
                    "public class LintViolation2Activity extends Activity { // lint violation\n" +
                    "             ~~~~~~~~~~~~~~~~~~~~~~\n" +
                    "1 errors, 0 warnings\n"
            )
    }

    @Test
    fun testLintViolation2Activity_kotlin() {
        val testFile = TestFiles.kotlin(
            "package me.ycdev.android.arch.demo.activity\n" +
                "\n" +
                "import android.app.Activity\n" +
                "import android.os.Bundle\n" +
                "\n" +
                "// class comment for test\n" +
                "class LintViolation2Activity : Activity() { // lint violation\n" +
                "    override fun onCreate(savedInstanceState: Bundle?) {\n" +
                "        super.onCreate(savedInstanceState)\n" +
                "    }\n" +
                "}\n"
        )
        lint().files(testFile)
            .issues(MyBaseActivityDetector.ISSUE)
            .run()
            .expect(
                "src/me/ycdev/android/arch/demo/activity/LintViolation2Activity.kt:7: Error: Please use the base classes for Activity. [MyBaseActivity]\n" +
                    "class LintViolation2Activity : Activity() { // lint violation\n" +
                    "      ~~~~~~~~~~~~~~~~~~~~~~\n" +
                    "1 errors, 0 warnings"
            )
    }
}
