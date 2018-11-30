package me.ycdev.android.lib.common.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FileDescriptor;

@SuppressWarnings({"unused", "WeakerAccess"})
public class ImageUtils {
    public interface IReusableBitmapProvider {
        Bitmap getReusableBitmap(@NonNull BitmapFactory.Options options);
    }

    /**
     * Decode and sample down a bitmap from resources to the requested width and height.
     *
     * @param res The resources object containing the image data
     * @param resId The resource id of the image data
     * @param reqWidth The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @param provider The IReusableBitmapProvider used to find candidate bitmaps for use with inBitmap.
     *                 Can be null if no bitmap reuse needed.
     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
     *         that are equal to or greater than the requested width and height
     */
    @Nullable
    public static Bitmap decodeSampledBitmapFromResource(@NonNull Resources res, @DrawableRes int resId,
            int reqWidth, int reqHeight, @Nullable IReusableBitmapProvider provider) {
        // Based on https://github.com/yongce/BitmapFun/blob/master/src/com/example/android/bitmapfun/util/ImageResizer.java

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Try to use inBitmap
        addInBitmapOptionsIfPossible(options, provider);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * Decode and sample down a bitmap from a file to the requested width and height.
     *
     * @param filename The full path of the file to decode
     * @param reqWidth The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @param provider The IReusableBitmapProvider used to find candidate bitmaps for use with inBitmap.
     *                 Can be null if no bitmap reuse needed.
     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
     *         that are equal to or greater than the requested width and height
     */
    @Nullable
    public static Bitmap decodeSampledBitmapFromFile(@NonNull String filename,
            int reqWidth, int reqHeight, @Nullable IReusableBitmapProvider provider) {
        // Based on https://github.com/yongce/BitmapFun/blob/master/src/com/example/android/bitmapfun/util/ImageResizer.java

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Try to use inBitmap
        addInBitmapOptionsIfPossible(options, provider);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filename, options);
    }

    /**
     * Decode and sample down a bitmap from a file input stream to the requested width and height.
     *
     * @param fileDescriptor The file descriptor to read from
     * @param reqWidth The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @param provider The IReusableBitmapProvider used to find candidate bitmaps for use with inBitmap.
     *                 Can be null if no bitmap reuse needed.
     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
     *         that are equal to or greater than the requested width and height
     */
    @Nullable
    public static Bitmap decodeSampledBitmapFromDescriptor(
            @NonNull FileDescriptor fileDescriptor, int reqWidth, int reqHeight,
            @Nullable IReusableBitmapProvider provider) {
        // Based on https://github.com/yongce/BitmapFun/blob/master/src/com/example/android/bitmapfun/util/ImageResizer.java

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        // Try to use inBitmap
        addInBitmapOptionsIfPossible(options, provider);

        return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
    }

    private static void addInBitmapOptionsIfPossible(@NonNull BitmapFactory.Options options,
            @Nullable IReusableBitmapProvider provider) {
        // Based on https://github.com/yongce/BitmapFun/blob/master/src/com/example/android/bitmapfun/util/ImageResizer.java

        if (provider == null) {
            return;
        }

        // inBitmap only works with mutable bitmaps so force the decoder to
        // return mutable bitmaps.
        options.inMutable = true;

        // Try and find a bitmap to use for inBitmap
        Bitmap inBitmap = provider.getReusableBitmap(options);
        if (inBitmap != null) {
            options.inBitmap = inBitmap;
        }
    }

    /**
     * Calculate an inSampleSize for use in a {@link android.graphics.BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link android.graphics.BitmapFactory}. This implementation calculates
     * the closest inSampleSize that will result in the final decoded bitmap having a width and
     * height equal to or larger than the requested width and height. This implementation does not
     * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
     * results in a larger bitmap which isn't as useful for caching purposes.
     *
     * @param options An options object with out* params already populated (run through a decode*
     *            method with #inJustDecodeBounds==true)
     * @param reqWidth The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    public static int calculateInSampleSize(@NonNull BitmapFactory.Options options,
            int reqWidth, int reqHeight) {
        // Based on https://github.com/yongce/BitmapFun/blob/master/src/com/example/android/bitmapfun/util/ImageResizer.java

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smaller ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            /*
             * @policy Please pay attention to the following policy.
             */

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }


    /**
     * Get the size in bytes of a bitmap.
     * @param bitmap The bitmap to calculate.
     * @return size in bytes
     */
    public static int getBitmapSize(@NonNull Bitmap bitmap) {
        return bitmap.getByteCount();
    }

}
