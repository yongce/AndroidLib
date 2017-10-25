package me.ycdev.android.arch.lint;

import com.android.tools.lint.checks.infrastructure.TestFile;
import com.android.tools.lint.checks.infrastructure.TestFiles;

import org.junit.Test;

import me.ycdev.android.arch.lint.utils.TestFileStubs;

import static com.android.tools.lint.checks.infrastructure.TestLintTask.lint;

public class MyIntentHelperDetectorTest {
    @Test
    public void testIntentHelperLintCase() {
        TestFile testFile = TestFiles.java("" +
                "package me.ycdev.android.arch.demo.wrapper;\n" +
                "\n" +
                "import android.content.Intent;\n" +
                "import android.os.Bundle;\n" +
                "\n" +
                "import me.ycdev.android.lib.common.wrapper.IntentHelper;\n" +
                "\n" +
                "public class IntentHelperLintCase {\n" +
                "    private static class Foo {\n" +
                "        public void hasExtra() { // lint good\n" +
                "        }\n" +
                "\n" +
                "        public void getBundleExtra() { // lint good\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    public static void hasExtra() { // lint good\n" +
                "        new Foo().hasExtra();\n" +
                "    }\n" +
                "\n" +
                "    public static void getBundleExtra() { // lint good\n" +
                "        new Foo().getBundleExtra();\n" +
                "    }\n" +
                "\n" +
                "    public static boolean hasExtraGood(Intent intent, String key) {\n" +
                "        return IntentHelper.hasExtra(intent, key); // lint good\n" +
                "    }\n" +
                "\n" +
                "    public static boolean getBooleanExtraGood(Intent intent, String key, boolean defValue) {\n" +
                "        return IntentHelper.getBooleanExtra(intent, key, defValue); // lint good\n" +
                "    }\n" +
                "\n" +
                "    public static Bundle getBundleExtraGood(Intent intent, String key) {\n" +
                "        return IntentHelper.getBundleExtra(intent, key); // lint good\n" +
                "    }\n" +
                "\n" +
                "    public static boolean hasExtraBad(Intent intent, String key) {\n" +
                "        return intent.hasExtra(key); // lint violation\n" +
                "    }\n" +
                "\n" +
                "    public static boolean getBooleanExtraBad(Intent intent, String key, boolean defValue) {\n" +
                "        return intent.getBooleanExtra(key, defValue); // lint violation\n" +
                "    }\n" +
                "\n" +
                "    public static Bundle getBundleExtraBad(Intent intent, String key) {\n" +
                "        return intent.getBundleExtra(key); // lint violation\n" +
                "    }\n" +
                "}\n");
        TestFile[] testFiles = new TestFile[] {
                TestFileStubs.getNonNull(), TestFileStubs.getNullable(),
                TestFileStubs.getLibLogger(), TestFileStubs.getIntentHelper(), testFile
        };
        lint().files(testFiles)
                .issues(MyIntentHelperDetector.ISSUE)
                .run()
                .expect("src/me/ycdev/android/arch/demo/wrapper/IntentHelperLintCase.java:38: Error: Please use the wrapper class 'IntentHelper'. [MyIntentHelper]\n" +
                        "        return intent.hasExtra(key); // lint violation\n" +
                        "               ~~~~~~~~~~~~~~~~~~~~\n" +
                        "src/me/ycdev/android/arch/demo/wrapper/IntentHelperLintCase.java:42: Error: Please use the wrapper class 'IntentHelper'. [MyIntentHelper]\n" +
                        "        return intent.getBooleanExtra(key, defValue); // lint violation\n" +
                        "               ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                        "src/me/ycdev/android/arch/demo/wrapper/IntentHelperLintCase.java:46: Error: Please use the wrapper class 'IntentHelper'. [MyIntentHelper]\n" +
                        "        return intent.getBundleExtra(key); // lint violation\n" +
                        "               ~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                        "3 errors, 0 warnings\n");
    }
}
