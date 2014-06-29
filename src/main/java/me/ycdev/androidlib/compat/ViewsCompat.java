package me.ycdev.androidlib.compat;

import me.ycdev.androidlib.utils.AndroidVersionUtils;

import android.annotation.TargetApi;
import android.os.Build;
import android.widget.ImageView;

public class ViewsCompat {
    /**
     * Set alpha of the image view
     * @param imageView
     * @param alpha [0~255]
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("deprecation")
    public static void setImageViewAlpha(ImageView imageView, int alpha) {
        if (AndroidVersionUtils.hasJellyBean()) {
            imageView.setImageAlpha(alpha);
        } else {
            imageView.setAlpha(alpha);
        }
    }
}
