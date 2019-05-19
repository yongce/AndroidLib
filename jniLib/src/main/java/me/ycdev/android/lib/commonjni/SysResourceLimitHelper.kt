package me.ycdev.android.lib.commonjni

object SysResourceLimitHelper {

    init {
        CommonJniLoader.load()
    }

    data class LimitInfo(var curLimit: Int = 0, var maxLimit: Int = 0)

    /**
     * Get the maximum number of open files for this process.
     * @return null if failed
     */
    external fun getOpenFilesLimit(): LimitInfo

    /**
     * Set the maximum number of open files for this process.
     * @param newLimit The new limit to set. Can NOT greater than the max limit.
     * @return true if successful
     */
    external fun setOpenFilesLimit(newLimit: Int): Boolean
}
