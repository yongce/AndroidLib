package me.ycdev.android.lib.common.utils;

import android.support.annotation.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressWarnings({"unused", "WeakerAccess"})
public class ReflectionUtils {
    public static Method findMethod(@NonNull Class<?> classObj, @NonNull String methodName,
            Class<?>... parameterTypes) throws NoSuchMethodException {
        // first, search public methods
        try {
            return classObj.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            // ignore
        }

        // next, search the non-public methods
        for (Class<?> c = classObj; c != null; c = c.getSuperclass()) {
            try {
                Method method = c.getDeclaredMethod(methodName, parameterTypes);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException e) {
                // ignore
            }
        }

        throw new NoSuchMethodException(methodName + " not found");
    }

    public static Field findField(@NonNull Class<?> classObj, @NonNull String fieldName)
            throws NoSuchFieldException {
        // first, search public fields
        try {
            return classObj.getField(fieldName);
        } catch (NoSuchFieldException e) {
            // ignore
        }

        // next, search non-public fields
        for (Class<?> c = classObj; c != null; c = c.getSuperclass()) {
            try {
                Field field = c.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                // ignore
            }
        }

        throw new NoSuchFieldException(fieldName + " not found");
    }
}
