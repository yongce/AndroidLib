package me.ycdev.android.lib.common.utils;

import androidx.annotation.NonNull;

@SuppressWarnings({"WeakerAccess", "unused"})
public class EncodingUtils {
    private static final char[] HEX_ARRAY_UPPERCASE = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final char[] HEX_ARRAY_LOWERCASE = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * Encode the data with HEX (Base16) encoding and with uppercase letters.
     */
    public static String encodeWithHex(byte[] bytes) {
        return encodeWithHex(bytes, true);
    }

    public static String encodeWithHex(byte[] bytes, boolean uppercase) {
        if (bytes == null) {
            return "null";
        }
        return encodeWithHex(bytes, 0, bytes.length, uppercase);
    }

    /**
     * Encode the data with HEX (Base16) encoding and with uppercase letters.
     */
    public static String encodeWithHex(@NonNull byte[] bytes, int startPos, int endPos) {
        return encodeWithHex(bytes, startPos, endPos, true);
    }

    public static String encodeWithHex(@NonNull byte[] bytes, int startPos, int endPos, boolean uppercase) {
        if (endPos > bytes.length) {
            endPos = bytes.length;
        }
        final int N = endPos - startPos;
        final char[] HEX_ARRAY = uppercase ? HEX_ARRAY_UPPERCASE : HEX_ARRAY_LOWERCASE;
        char[] hexChars = new char[N * 2];
        for (int i = startPos, j = 0; i < endPos; i++, j += 2) {
            int v = bytes[i] & 0xFF;
            hexChars[j] = HEX_ARRAY[v >>> 4];
            hexChars[j + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] fromHexString(@NonNull String hexStr) {
        hexStr = hexStr.replace(" ", ""); // support spaces
        if (hexStr.length() % 2 != 0) {
            throw new IllegalArgumentException("Bad length: " + hexStr);
        }

        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < result.length; i++) {
            int high = fromHexChar(hexStr, i * 2) << 4;
            int low = fromHexChar(hexStr, i * 2 + 1);
            result[i] = (byte) ((high | low) & 0xFF);
        }
        return result;
    }

    private static int fromHexChar(String hexStr, int index) {
        char ch = hexStr.charAt(index);
        if (ch >= '0' && ch <= '9') {
            return ch - '0';
        } else if (ch >= 'a' && ch <= 'f') {
            return 10 + (ch - 'a');
        } else if (ch >= 'A' && ch <= 'F') {
            return 10 + (ch - 'A');
        } else {
            throw new IllegalArgumentException("Not hex string: " + hexStr);
        }
    }
}
