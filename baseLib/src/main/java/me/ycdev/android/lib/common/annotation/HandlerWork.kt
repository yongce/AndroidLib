package me.ycdev.android.lib.common.annotation

/**
 * Denotes that the annotated method can only be executed in the specified handler.
 */
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.SOURCE)
annotation class HandlerWork(val value: String)
