package me.ycdev.android.lib.commonjni;

public class SysResourceLimitHelper {
    static {
        CommonJniLoader.load();
    }

    public static class LimitInfo {
        public int curLimit;
        public int maxLimit;
    }

    /**
     * Get the maximum number of open files for this process.
     * @return null if failed
     */
    public static native LimitInfo getOpenFilesLimit();

    /**
     * Set the maximum number of open files for this process.
     * @param newLimit The new limit to set. Can NOT greater than the max limit.
     * @return true if successful
     */
    public static native boolean setOpenFilesLimit(int newLimit);
}
