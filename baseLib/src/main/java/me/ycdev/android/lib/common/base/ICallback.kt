package me.ycdev.android.lib.common.base

@FunctionalInterface
interface ICallback {
    fun callback(vararg params: Any)
}
