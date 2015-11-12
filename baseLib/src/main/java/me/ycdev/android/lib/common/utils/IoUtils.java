package me.ycdev.android.lib.common.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.zip.ZipFile;

public class IoUtils {
    private static final int IO_BUF_SIZE = 1024 * 16; // 16KB

    private IoUtils() {
    }

    /**
     * Close the closeable target and eat possible exceptions.
     * @param target The target to close. Can be null.
     */
    public static void closeQuietly(@Nullable Closeable target) {
        try {
            if (target != null) {
                target.close();
            }
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * Before Android 4.4, ZipFile doesn't implement the interface "java.io.Closeable".
     * @param target The target to close. Can be null.
     */
    public static void closeQuietly(@Nullable ZipFile target) {
        try {
            if (target != null) target.close();
        } catch (IOException e) {
            // ignore
        }
    }

    public static byte[] readAllBytes(@NonNull InputStream is) throws IOException {
        ByteArrayOutputStream bytesBuf = new ByteArrayOutputStream(1024);
        int bytesReaded;
        byte[] buf = new byte[1024];
        while ((bytesReaded = is.read(buf, 0, buf.length)) != -1) {
            bytesBuf.write(buf, 0, bytesReaded);
        }
        return bytesBuf.toByteArray();
    }

    /**
     * Read all lines of the stream as a String.
     * Use the "UTF-8" character converter when reading.
     * @return May be empty String, but never null.
     */
    @NonNull
    public static String readAllLines(@NonNull InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        boolean first = true;

        while ((line = reader.readLine()) != null) {
            if (!first) {
                sb.append('\n');
            } else {
                first = false;
            }
            sb.append(line);
        }

        return sb.toString();
    }

    /**
     * Read all lines of the text file as a String.
     * @param filePath The file to read
     */
    @NonNull
    public static String readAllLines(@NonNull String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        try {
            return readAllLines(fis);
        } finally {
            closeQuietly(fis);
        }
    }

    public static void createParentDirsIfNeeded(@NonNull File file) {
        File dirFile = file.getParentFile();
        if (dirFile != null && !dirFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dirFile.mkdirs();
        }
    }

    public static void createParentDirsIfNeeded(@NonNull String filePath) {
        createParentDirsIfNeeded(new File(filePath));
    }

    public static void saveAsFile(@NonNull String content, @NonNull String filePath)
            throws IOException {
        FileWriter fw = new FileWriter(filePath);
        try {
            fw.write(content);
            fw.flush();
        } finally {
            closeQuietly(fw);
        }
    }

    /**
     * Save the input stream into a file.</br>
     * Note: This method will not close the input stream.
     */
    public static void saveAsFile(@NonNull InputStream is, @NonNull String filePath)
            throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath);
        try {
            copyStream(is, fos);
        } finally {
            closeQuietly(fos);
        }
    }

    /**
     * Copy data from the input stream to the output stream.</br>
     * Note: This method will not close the input stream and output stream.
     */
    public static void copyStream(@NonNull InputStream is, @NonNull OutputStream os)
            throws IOException {
        byte[] buffer = new byte[IO_BUF_SIZE];
        int len;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        os.flush();
    }

    public static void copyFile(@NonNull String srcFilePath, @NonNull String destFilePath)
            throws IOException {
        FileInputStream fis = new FileInputStream(srcFilePath);
        try {
            saveAsFile(fis, destFilePath);
        } finally {
            closeQuietly(fis);
        }
    }
}
