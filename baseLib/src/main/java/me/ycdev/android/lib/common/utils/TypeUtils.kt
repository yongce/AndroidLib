package me.ycdev.android.lib.common.utils

import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import java.lang.reflect.WildcardType

object TypeUtils {
    fun getRawType(type: Type): Class<*> {
        if (type is Class<*>) {
            // Type is a normal class.
            return type
        }
        if (type is ParameterizedType) {
            val parameterizedType: ParameterizedType = type

            // I'm not exactly sure why getRawType() returns Type instead of Class. Neal isn't either but
            // suspects some pathological case related to nested classes exists.
            val rawType: Type = parameterizedType.rawType
            require(rawType is Class<*>)
            return rawType
        }
        if (type is GenericArrayType) {
            val componentType: Type = type.genericComponentType
            return java.lang.reflect.Array.newInstance(getRawType(componentType), 0).javaClass
        }
        if (type is TypeVariable<*>) {
            // We could use the variable's bounds, but that won't work if there are multiple. Having a raw
            // type that's more general than necessary is okay.
            return Any::class.java
        }
        if (type is WildcardType) {
            return getRawType(type.upperBounds[0])
        }
        throw IllegalArgumentException(
            "Expected a Class, ParameterizedType, or " +
                    "GenericArrayType, but <" +
                    type +
                    "> is of type " +
                    type.javaClass.name
        )
    }
}
