package me.ycdev.android.lib.common.type;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class LongHolderTest {
    @Test
    public void basic() {
        {
            LongHolder holder = new LongHolder(0L);
            assertThat(holder.value, is(0L));
        }

        {
            LongHolder holder = new LongHolder(-10L);
            assertThat(holder.value, is(-10L));
        }

        {
            LongHolder holder = new LongHolder(100L);
            assertThat(holder.value, is(100L));
        }
    }
}
