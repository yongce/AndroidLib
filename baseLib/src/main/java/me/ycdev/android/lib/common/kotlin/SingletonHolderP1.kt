package me.ycdev.android.lib.common.kotlin

open class SingletonHolderP1<out T, in P>(private val creator: (P) -> T) {
    @Volatile
    private var instance: T? = null

    fun getInstance(param: P): T =
        instance ?: synchronized(this) {
            instance ?: creator(param).also { instance = it }
        }
}
