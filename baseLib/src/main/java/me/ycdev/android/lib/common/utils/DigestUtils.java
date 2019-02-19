package me.ycdev.android.lib.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static me.ycdev.android.lib.common.utils.EncodingUtils.encodeWithHex;

@SuppressWarnings({"WeakerAccess", "unused"})
public class DigestUtils {
    public static String md5(final String text)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return hash(text, "MD5");
    }

    public static String md5(byte[] data) throws NoSuchAlgorithmException {
        return hash(data, "MD5");
    }

    public static String sha1(String text)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return hash(text, "SHA-1");
    }

    public static String sha1(byte[] data)
            throws NoSuchAlgorithmException {
        return hash(data, "SHA-1");
    }

    public static String hash(String text, String algorithm)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return hash(text.getBytes("UTF-8"), algorithm);
    }

    public static String hash(byte[] data, String algorithm)
            throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.update(data);
        byte messageDigest[] = digest.digest();
        return encodeWithHex(messageDigest, false);
    }

    /**
     * The caller should close the stream.
     */
    public static String md5(final InputStream stream)
            throws NoSuchAlgorithmException, IOException {
        if (stream == null) {
            throw new IllegalArgumentException("Invalid input stream!");
        }
        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;
        do {
            numRead = stream.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);
        byte[] digest = complete.digest();
        return encodeWithHex(digest, false);
    }

    public static String md5(final File file) throws NoSuchAlgorithmException, IOException {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
            return md5(stream);
        } finally {
            IoUtils.closeQuietly(stream);
        }
    }
}
