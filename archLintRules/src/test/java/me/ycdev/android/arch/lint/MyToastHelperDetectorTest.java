package me.ycdev.android.arch.lint;

import com.android.tools.lint.checks.infrastructure.TestFile;
import com.android.tools.lint.checks.infrastructure.TestFiles;

import org.junit.Test;

import me.ycdev.android.arch.lint.utils.TestFileStubs;

import static com.android.tools.lint.checks.infrastructure.TestLintTask.lint;

public class MyToastHelperDetectorTest {
    @Test
    public void testToastHelperLintCase() {
        TestFile testFile = TestFiles.java("" +
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
                "}\n");
        TestFile[] testFiles = new TestFile[] {
                TestFileStubs.getNonNull(), TestFileStubs.getNullable(),
                TestFileStubs.getStringRes(), TestFileStubs.getToastHelper(), testFile
        };
        lint().files(testFiles)
                .issues(MyToastHelperDetector.ISSUE)
                .run()
                .expect("src/me/ycdev/android/arch/demo/wrapper/ToastHelperLintCase.java:34: Error: Please use the wrapper class 'ToastHelper'. [MyToastHelper]\n" +
                        "        Toast.makeText(cxt, msgResId, duration).show(); // lint violation\n" +
                        "        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                        "src/me/ycdev/android/arch/demo/wrapper/ToastHelperLintCase.java:38: Error: Please use the wrapper class 'ToastHelper'. [MyToastHelper]\n" +
                        "        Toast.makeText(cxt, msg, duration).show(); // lint violation\n" +
                        "        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                        "2 errors, 0 warnings\n");
    }
}
