package me.ycdev.android.lib.common.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes that the class, method or field has its visibility relaxed so
 * that unit tests can access it.
 * <p/>
 * The <code>visibility</code> argument can be used to specific what the original
 * visibility should have been if it had not been made public or package-private for testing.
 * The default is to consider the element private.
 * <p/>
 * <i>Note: Copied from com.android.internal.annotations.Immutable.VisibleForTesting.</i>
 */
@Documented
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.SOURCE)
public @interface VisibleForTesting {
    /**
     * Intended visibility if the element had not been made public or package-private for
     * testing.
     */
    enum Visibility {
        /** The element should be considered protected. */
        PROTECTED,
        /** The element should be considered package-private. */
        PACKAGE,
        /** The element should be considered private. */
        PRIVATE
    }

    /**
     * Intended visibility if the element had not been made public or package-private for testing.
     * If not specified, one should assume the element originally intended to be private.
     */
    Visibility visibility() default Visibility.PRIVATE;
}

