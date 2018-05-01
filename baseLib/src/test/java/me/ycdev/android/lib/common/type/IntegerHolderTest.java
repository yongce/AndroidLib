package me.ycdev.android.lib.common.type;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class IntegerHolderTest {
    @Test
    public void basic() {
        {
            IntegerHolder holder = new IntegerHolder(0);
            assertThat(holder.value, is(0));
        }

        {
            IntegerHolder holder = new IntegerHolder(-10);
            assertThat(holder.value, is(-10));
        }

        {
            IntegerHolder holder = new IntegerHolder(100);
            assertThat(holder.value, is(100));
        }
    }
}
