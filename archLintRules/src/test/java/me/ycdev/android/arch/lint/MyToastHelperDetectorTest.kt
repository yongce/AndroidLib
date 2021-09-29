package me.ycdev.android.arch.lint

import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import me.ycdev.android.arch.lint.utils.TestFileStubs
import org.junit.Test

class MyToastHelperDetectorTest {
    @Test
    fun testToastHelperLintCase_java() {
        val testFile = TestFiles.java(
            "" +
                    "package me.ycdev.android.arch.demo.wrapper;\n" +
                    "\n" +
                    "import android.content.Context;\n" +
                    "import android.widget.Toast;\n" +
                    "\n" +
                    "import me.ycdev.android.arch.wrapper.ToastHelper;\n" +
                    "\n" +
                    "public class ToastHelperLintCase {\n" +
                    "    private static class Foo {\n" +
                    "        public void show() { // lint good\n" +
                    "        }\n" +
                    "\n" +
                    "        public void makeText() { // lint good\n" +
                    "        }\n" +
                    "    }\n" +
                    "\n" +
                    "    public static void show() { // lint good\n" +
                    "        new Foo().show();\n" +
                    "    }\n" +
                    "\n" +
                    "    public static void makeText() { // lint good\n" +
                    "        new Foo().makeText();\n" +
                    "    }\n" +
                    "\n" +
                    "    public static void showGood(Context cxt, int msgResId, int duration) {\n" +
                    "        ToastHelper.show(cxt, msgResId, duration); // lint good\n" +
                    "    }\n" +
                    "\n" +
                    "    public static void showGood(Context cxt, CharSequence msg, int duration) {\n" +
                    "        ToastHelper.show(cxt, msg, duration); // lint good\n" +
                    "    }\n" +
                    "\n" +
                    "    public static void showViolation(Context cxt, int msgResId, int duration) {\n" +
                    "        Toast.makeText(cxt, msgResId, duration).show(); // lint violation\n" +
                    "    }\n" +
                    "\n" +
                    "    public static void showViolation(Context cxt, CharSequence msg, int duration) {\n" +
                    "        Toast.makeText(cxt, msg, duration).show(); // lint violation\n" +
                    "    }\n" +
                    "}\n"
        )
        val testFiles = arrayOf<TestFile>(
            TestFileStubs.nonNull,
            TestFileStubs.nullable,
            TestFileStubs.stringRes,
            TestFileStubs.toastHelper,
            testFile
        )
        lint().files(*testFiles)
            .issues(MyToastHelperDetector.ISSUE)
            .run()
            .expect(
                "src/me/ycdev/android/arch/demo/wrapper/ToastHelperLintCase.java:34: Error: Please use the wrapper class 'ToastHelper'. [MyToastHelper]\n" +
                        "        Toast.makeText(cxt, msgResId, duration).show(); // lint violation\n" +
                        "        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                        "src/me/ycdev/android/arch/demo/wrapper/ToastHelperLintCase.java:38: Error: Please use the wrapper class 'ToastHelper'. [MyToastHelper]\n" +
                        "        Toast.makeText(cxt, msg, duration).show(); // lint violation\n" +
                        "        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                        "2 errors, 0 warnings\n"
            )
    }

    @Test
    fun testToastHelperLintCase_kotlin() {
        val testFile = TestFiles.kotlin(
            "package me.ycdev.android.arch.demo.wrapper\n" +
                    "\n" +
                    "import android.content.Context\n" +
                    "import android.widget.Toast\n" +
                    "\n" +
                    "import me.ycdev.android.arch.wrapper.ToastHelper\n" +
                    "\n" +
                    "object ToastHelperLintCase {\n" +
                    "    private class Foo {\n" +
                    "        fun show() { // lint good\n" +
                    "        }\n" +
                    "\n" +
                    "        fun makeText() { // lint good\n" +
                    "        }\n" +
                    "    }\n" +
                    "\n" +
                    "    fun show() { // lint good\n" +
                    "        Foo().show()\n" +
                    "    }\n" +
                    "\n" +
                    "    fun makeText() { // lint good\n" +
                    "        Foo().makeText()\n" +
                    "    }\n" +
                    "\n" +
                    "    fun showGood(cxt: Context, msgResId: Int, duration: Int) {\n" +
                    "        ToastHelper.show(cxt, msgResId, duration) // lint good\n" +
                    "    }\n" +
                    "\n" +
                    "    fun showGood(cxt: Context, msg: CharSequence, duration: Int) {\n" +
                    "        ToastHelper.show(cxt, msg, duration) // lint good\n" +
                    "    }\n" +
                    "\n" +
                    "    fun showViolation(cxt: Context, msgResId: Int, duration: Int) {\n" +
                    "        Toast.makeText(cxt, msgResId, duration).show() // lint violation\n" +
                    "    }\n" +
                    "\n" +
                    "    fun showViolation(cxt: Context, msg: CharSequence, duration: Int) {\n" +
                    "        Toast.makeText(cxt, msg, duration).show() // lint violation\n" +
                    "    }\n" +
                    "}\n"
        )
        val testFiles = arrayOf<TestFile>(
            TestFileStubs.stringRes,
            TestFileStubs.nonNull,
            TestFileStubs.toastHelper,
            testFile
        )
        lint().files(*testFiles)
            .issues(MyToastHelperDetector.ISSUE)
            .run()
            .expect(
                "src/me/ycdev/android/arch/demo/wrapper/ToastHelperLintCase.kt:34: Error: Please use the wrapper class 'ToastHelper'. [MyToastHelper]\n" +
                        "        Toast.makeText(cxt, msgResId, duration).show() // lint violation\n" +
                        "        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                        "src/me/ycdev/android/arch/demo/wrapper/ToastHelperLintCase.kt:38: Error: Please use the wrapper class 'ToastHelper'. [MyToastHelper]\n" +
                        "        Toast.makeText(cxt, msg, duration).show() // lint violation\n" +
                        "        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                        "2 errors, 0 warnings"
            )
    }
}
