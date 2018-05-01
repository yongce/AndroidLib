package me.ycdev.android.lib.common.type;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class BooleanHolderTest {
    @Test
    public void basic() {
        {
            BooleanHolder holder = new BooleanHolder(true);
            assertThat(holder.value, is(true));
        }

        {
            BooleanHolder holder = new BooleanHolder(false);
            assertThat(holder.value, is(false));
        }
    }
}
