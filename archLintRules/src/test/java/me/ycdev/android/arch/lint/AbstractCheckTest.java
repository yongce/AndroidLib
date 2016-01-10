package me.ycdev.android.arch.lint;

import com.android.tools.lint.checks.infrastructure.LintDetectorTest;
import com.android.utils.SdkUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;

public abstract class AbstractCheckTest extends LintDetectorTest {
    @Override
    protected InputStream getTestResource(String relativePath, boolean expectExists) {
        String path = "archLintRulesTestDemo/src/main/" + relativePath;
        File root = getTestDataRootDir();
        assertNotNull(root);
        File f = new File(root, path);
        System.out.println("test file: " + f.getAbsolutePath());
        if (f.exists()) {
            try {
                return new BufferedInputStream(new FileInputStream(f));
            } catch (FileNotFoundException e) {
                if (expectExists) {
                    fail("Could not find file " + relativePath);
                }
            }
        }
        return null;
    }

    private File getTestDataRootDir() {
        CodeSource source = getClass().getProtectionDomain().getCodeSource();
        if (source != null) {
            URL location = source.getLocation();
            try {
                File classesDir = SdkUtils.urlToFile(location);
                // "AndroidArch/archLintRules/build/classes/test" --> "AndroidArch"
                return classesDir.getParentFile().getAbsoluteFile().getParentFile()
                        .getParentFile().getParentFile();
            } catch (MalformedURLException e) {
                fail(e.getLocalizedMessage());
            }
        }
        return null;
    }
}
