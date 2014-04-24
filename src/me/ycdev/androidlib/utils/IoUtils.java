package me.ycdev.androidlib.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IoUtils {
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

    public static void saveAsFile(String content, String filePath) throws IOException {
        FileWriter fw = new FileWriter(filePath);
        try {
            fw.write(content);
            fw.flush();
        } finally {
            closeQuietly(fw);
        }
    }
}
