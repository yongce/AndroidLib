package me.ycdev.android.lib.commonjni;

public class FileStatusHelper {
    static {
        CommonJniLoader.load();
    }

    public static class FileStatus {
        public int uid;
        public int gid;
        public int mode;
    }

    public static native FileStatus getFileStatus(String filePath);

}
