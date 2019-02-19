package me.ycdev.android.lib.common.utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

public class EncodingUtilsTest {
    @Rule
    public ExpectedException thrownRule = ExpectedException.none();

    @Test
    public void encodeWithHex() {
        byte[] data = new byte[] {0x1a, (byte)0x2b, 0x3c, 0x4d, (byte)0x5c, 0x6d, (byte)0x7e};
        String result = EncodingUtils.encodeWithHex(data, 0, data.length);
        assertThat(result, equalTo("1A2B3C4D5C6D7E"));
        result = EncodingUtils.encodeWithHex(data, 1, 4, true);
        assertThat(result, equalTo("2B3C4D"));
        result = EncodingUtils.encodeWithHex(data, 3, 20);
        assertThat(result, equalTo("4D5C6D7E"));

        // lowercase
        result = EncodingUtils.encodeWithHex(data, 0, data.length, false);
        assertThat(result, equalTo("1a2b3c4d5c6d7e"));
        result = EncodingUtils.encodeWithHex(data, 1, 4, false);
        assertThat(result, equalTo("2b3c4d"));
        result = EncodingUtils.encodeWithHex(data, 3, 20, false);
        assertThat(result, equalTo("4d5c6d7e"));
    }

    @Test
    public void test_fromHexString() {
        String hexStr = "01020304050607";
        String hexStr2 = " 010 20 30 405 060 7 ";
        String hexStr3 = "010 203 040 506 07";
        byte[] data = new byte[] {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};
        assertThat(EncodingUtils.fromHexString(hexStr), equalTo(data));
        assertThat(EncodingUtils.fromHexString(hexStr2), equalTo(data));
        assertThat(EncodingUtils.fromHexString(hexStr3), equalTo(data));
    }

    @Test
    public void test_illegalLength() {
        thrownRule.expect(IllegalArgumentException.class);
        thrownRule.expectMessage(startsWith("Bad length: 10101"));

        String hexStr = "10101";
        EncodingUtils.fromHexString(hexStr);
    }

    @Test
    public void test_illegalCharacter() {
        thrownRule.expect(IllegalArgumentException.class);
        thrownRule.expectMessage(startsWith("Not hex string: 10101X"));

        String hexStr = "10101X";
        EncodingUtils.fromHexString(hexStr);
    }
}
