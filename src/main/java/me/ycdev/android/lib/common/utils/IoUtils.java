package me.ycdev.android.lib.common.utils;

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

public class IoUtils {
    private static final int IO_BUF_SIZE = 1024 * 16; // 16KB

    private IoUtils() {
    }

    /**
     * Close the closeable target and eat possible exceptions.
     * @param target The target to close. Can be null.
     */
    public static void closeQuietly(Closeable target) {
        try {
            if (target != null) {
                target.close();
            }
        } catch (Exception e) {
            // ignore
        }
    }

    public static byte[] readAllBytes(InputStream is) throws IOException {
        ByteArrayOutputStream bytesBuf = new ByteArrayOutputStream(1024);
        int bytesReaded = 0;
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
    public static String readAllLines(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line = null;
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
    public static String readAllLines(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        try {
            return readAllLines(fis);
        } finally {
            closeQuietly(fis);
        }
    }

    public static void createParentDirsIfNeeded(File file) {
        File dirFile = file.getParentFile();
        if (dirFile != null && !dirFile.exists()) {
            dirFile.mkdirs();
        }
    }

    public static void createParentDirsIfNeeded(String filePath) {
        createParentDirsIfNeeded(new File(filePath));
    }

    public static void saveAsFile(String content, String filePath) throws IOException {
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
    public static void saveAsFile(InputStream is, String filePath) throws IOException {
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
    public static void copyStream(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[IO_BUF_SIZE];
        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        os.flush();
    }

    public static void copyFile(String srcFilePath, String destFilePath) throws IOException {
        FileInputStream fis = new FileInputStream(srcFilePath);
        try {
            saveAsFile(fis, destFilePath);
        } finally {
            closeQuietly(fis);
        }
    }
}
