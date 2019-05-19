package me.ycdev.android.lib.common.utils

import java.lang.reflect.Field
import java.lang.reflect.Method

object ReflectionUtils {
    @Throws(NoSuchMethodException::class)
    fun findMethod(
        classObj: Class<*>,
        methodName: String,
        vararg parameterTypes: Class<*>
    ): Method {
        // first, search public methods
        try {
            return classObj.getMethod(methodName, *parameterTypes)
        } catch (e: NoSuchMethodException) {
            // ignore
        }

        // next, search the non-public methods
        var c: Class<*>? = classObj
        while (c != null) {
            try {
                val method = c.getDeclaredMethod(methodName, *parameterTypes)
                method.isAccessible = true
                return method
            } catch (e: NoSuchMethodException) {
                // ignore
            }

            c = c.superclass
        }

        throw NoSuchMethodException("$methodName not found")
    }

    @Throws(NoSuchFieldException::class)
    fun findField(classObj: Class<*>, fieldName: String): Field {
        // first, search public fields
        try {
            return classObj.getField(fieldName)
        } catch (e: NoSuchFieldException) {
            // ignore
        }

        // next, search non-public fields
        var c: Class<*>? = classObj
        while (c != null) {
            try {
                val field = c.getDeclaredField(fieldName)
                field.isAccessible = true
                return field
            } catch (e: NoSuchFieldException) {
                // ignore
            }

            c = c.superclass
        }

        throw NoSuchFieldException("$fieldName not found")
    }
}
