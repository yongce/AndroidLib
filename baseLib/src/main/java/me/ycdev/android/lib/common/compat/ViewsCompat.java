package me.ycdev.android.lib.common.compat;

import me.ycdev.android.lib.common.utils.AndroidVersionUtils;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.ImageView;

public class ViewsCompat {
    /**
     * Set alpha of the image view
     * @param imageView
     * @param alpha [0~255]
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("deprecation")
    public static void setImageViewAlpha(@NonNull ImageView imageView, int alpha) {
        if (AndroidVersionUtils.hasJellyBean()) {
            imageView.setImageAlpha(alpha);
        } else {
            imageView.setAlpha(alpha);
        }
    }
}
