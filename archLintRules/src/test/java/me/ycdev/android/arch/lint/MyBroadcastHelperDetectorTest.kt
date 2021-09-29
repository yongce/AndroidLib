package me.ycdev.android.arch.lint

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import me.ycdev.android.arch.lint.utils.TestFileStubs
import org.junit.Test

class MyBroadcastHelperDetectorTest {
    @Test
    @Throws(Exception::class)
    fun testBroadcastHelperLintCase_java() {
        val testFile = TestFiles.java(
            "" +
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
                    "}\n"
        )
        lint().files(TestFileStubs.nonNull, TestFileStubs.broadcastHelper, testFile)
            .issues(MyBroadcastHelperDetector.ISSUE)
            .run()
            .expect(
                "src/me/ycdev/android/arch/demo/wrapper/BroadcastHelperLintCase.java:44: Error: Please use the wrapper class 'BroadcastHelper'. [MyBroadcastHelper]\n" +
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
                        "4 errors, 0 warnings\n"
            )
    }

    @Test
    @Throws(Exception::class)
    fun testBroadcastHelperLintCase_kotlin() {
        val testFile = TestFiles.kotlin(
            "package me.ycdev.android.arch.demo.wrapper\n" +
                    "\n" +
                    "import android.content.BroadcastReceiver\n" +
                    "import android.content.Context\n" +
                    "import android.content.Intent\n" +
                    "import android.content.IntentFilter\n" +
                    "\n" +
                    "import me.ycdev.android.lib.common.wrapper.BroadcastHelper\n" +
                    "\n" +
                    "object BroadcastHelperLintCase {\n" +
                    "    private class Foo {\n" +
                    "        fun registerReceiver() { // lint good\n" +
                    "        }\n" +
                    "\n" +
                    "        fun sendBroadcast() { // lint good\n" +
                    "        }\n" +
                    "    }\n" +
                    "\n" +
                    "    fun registerReceiver() { // lint good\n" +
                    "        Foo().registerReceiver()\n" +
                    "    }\n" +
                    "\n" +
                    "    fun sendBroadcast() { // lint good\n" +
                    "        Foo().sendBroadcast()\n" +
                    "    }\n" +
                    "\n" +
                    "    fun registerGood(cxt: Context, receiver: BroadcastReceiver, filter: IntentFilter): Intent? {\n" +
                    "        return BroadcastHelper.registerForInternal(cxt, receiver, filter) // lint good\n" +
                    "    }\n" +
                    "\n" +
                    "    fun sendToInternalGood(cxt: Context, intent: Intent) {\n" +
                    "        BroadcastHelper.sendToInternal(cxt, intent) // lint good\n" +
                    "    }\n" +
                    "\n" +
                    "    fun sendToExternalGood(cxt: Context, intent: Intent, perm: String) {\n" +
                    "        BroadcastHelper.sendToExternal(cxt, intent, perm) // lint good\n" +
                    "    }\n" +
                    "\n" +
                    "    fun sendToExternal(cxt: Context, intent: Intent) {\n" +
                    "        BroadcastHelper.sendToExternal(cxt, intent) // lint good\n" +
                    "    }\n" +
                    "\n" +
                    "    fun registerViolation(\n" +
                    "        cxt: Context,\n" +
                    "        receiver: BroadcastReceiver,\n" +
                    "        filter: IntentFilter\n" +
                    "    ): Intent? {\n" +
                    "        return cxt.registerReceiver(receiver, filter) // lint violation\n" +
                    "    }\n" +
                    "\n" +
                    "    fun registerViolation2(\n" +
                    "        cxt: Context,\n" +
                    "        receiver: BroadcastReceiver,\n" +
                    "        filter: IntentFilter\n" +
                    "    ): Intent? {\n" +
                    "        return cxt.registerReceiver(receiver, filter, null, null) // lint violation\n" +
                    "    }\n" +
                    "\n" +
                    "    fun sendViolation(cxt: Context, intent: Intent, perm: String) {\n" +
                    "        cxt.sendBroadcast(intent, perm) // lint violation\n" +
                    "    }\n" +
                    "\n" +
                    "    fun sendViolation2(cxt: Context, intent: Intent) {\n" +
                    "        cxt.sendBroadcast(intent) // lint violation\n" +
                    "    }\n" +
                    "}\n"
        )
        lint().files(TestFileStubs.nonNull, TestFileStubs.broadcastHelper, testFile)
            .issues(MyBroadcastHelperDetector.ISSUE)
            .run()
            .expect(
                "src/me/ycdev/android/arch/demo/wrapper/BroadcastHelperLintCase.kt:48: Error: Please use the wrapper class 'BroadcastHelper'. [MyBroadcastHelper]\n" +
                        "        return cxt.registerReceiver(receiver, filter) // lint violation\n" +
                        "               ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                        "src/me/ycdev/android/arch/demo/wrapper/BroadcastHelperLintCase.kt:56: Error: Please use the wrapper class 'BroadcastHelper'. [MyBroadcastHelper]\n" +
                        "        return cxt.registerReceiver(receiver, filter, null, null) // lint violation\n" +
                        "               ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                        "src/me/ycdev/android/arch/demo/wrapper/BroadcastHelperLintCase.kt:60: Error: Please use the wrapper class 'BroadcastHelper'. [MyBroadcastHelper]\n" +
                        "        cxt.sendBroadcast(intent, perm) // lint violation\n" +
                        "        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                        "src/me/ycdev/android/arch/demo/wrapper/BroadcastHelperLintCase.kt:64: Error: Please use the wrapper class 'BroadcastHelper'. [MyBroadcastHelper]\n" +
                        "        cxt.sendBroadcast(intent) // lint violation\n" +
                        "        ~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                        "4 errors, 0 warnings"
            )
    }

    @Test
    @Throws(Exception::class)
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
                    "}\n"
        )
        lint().files(TestFileStubs.appCompatActivity, testFile)
            .issues(MyBroadcastHelperDetector.ISSUE)
            .run()
            .expect(
                "src/me/ycdev/android/arch/demo/activity/LintViolationActivity.java:32: Error: Please use the wrapper class 'BroadcastHelper'. [MyBroadcastHelper]\n" +
                        "        registerReceiver(mReceiver, filter); // lint violation\n" +
                        "        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                        "src/me/ycdev/android/arch/demo/activity/LintViolationActivity.java:42: Error: Please use the wrapper class 'BroadcastHelper'. [MyBroadcastHelper]\n" +
                        "        sendBroadcast(new Intent(TEST_ACTION)); // lint violation\n" +
                        "        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                        "2 errors, 0 warnings\n"
            )
    }

    @Test
    @Throws(Exception::class)
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
                    "        private val TEST_ACTION = \"action.test\"\n" +
                    "    }\n" +
                    "}\n"
        )
        lint().files(TestFileStubs.appCompatActivityAndroidX, testFile)
            .issues(MyBroadcastHelperDetector.ISSUE)
            .run()
            .expect(
                "src/me/ycdev/android/arch/demo/activity/LintViolationActivity.kt:27: Error: Please use the wrapper class 'BroadcastHelper'. [MyBroadcastHelper]\n" +
                        "        registerReceiver(receiver, filter) // lint violation\n" +
                        "        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                        "src/me/ycdev/android/arch/demo/activity/LintViolationActivity.kt:36: Error: Please use the wrapper class 'BroadcastHelper'. [MyBroadcastHelper]\n" +
                        "        sendBroadcast(Intent(TEST_ACTION)) // lint violation\n" +
                        "        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                        "2 errors, 0 warnings"
            )
    }
}
