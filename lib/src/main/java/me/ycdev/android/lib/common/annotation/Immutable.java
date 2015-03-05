package me.ycdev.android.lib.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation type used to mark a class which is immutable.
 * <p/>
 * <i>Note: Copied from com.android.internal.annotations.Immutable.VisibleForTesting.</i>
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Immutable {
}
