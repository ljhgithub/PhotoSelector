package com.ljh.photoselector.util.media;

import com.ljh.photoselector.util.LogUtils;

/**
 * Created by ljh on 2016/3/16.
 */
public class SampleSizeUtil {
    public static final String TAG=LogUtils.makeLogTag(SampleSizeUtil.class.getSimpleName());

    public static int calculateSampleSize(String imagePath, int totalPixel) {
        int[] bound = BitmapDecoder.decodeBound(imagePath);
        LogUtils.LOGD(TAG,"width: "+bound[0]+"height: "+bound[1]);
        return calculateSampleSize(bound[0], bound[1], totalPixel);
    }

    private static int calculateSampleSize(int width, int height, int totalPixel) {
        int ratio = 1;
        if (width > 0 && height > 0) {
            ratio = (int) Math.sqrt((width * height) / totalPixel);
            if (ratio < 1) {
                ratio = 1;
            }
        }
        LogUtils.LOGD(TAG,"ratio: "+ratio+" totalPixel: "+totalPixel);
        return ratio;
    }

    /**
     * Calculate an inSampleSize for use in a {@link android.graphics.BitmapFactory.Options}
     * object when decoding bitmaps using the decode* methods from
     * {@link android.graphics.BitmapFactory}. This implementation calculates the closest
     * inSampleSize that will result in the final decoded bitmap having a width
     * and height equal to or larger than the requested width and height. This
     * implementation does not ensure a power of 2 is returned for inSampleSize
     * which can be faster when decoding but results in a larger bitmap which
     * isn't as useful for caching purposes.
     *
     * @param width
     * @param height
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateSampleSize(int width, int height, int reqWidth, int reqHeight) {
        // can't proceed
        if (width <= 0 || height <= 0) {
            return 1;
        }
        // can't proceed
        if (reqWidth <= 0 && reqHeight <= 0) {
            return 1;
        } else if (reqWidth <= 0) {
            reqWidth = (int) (width * reqHeight / (float)height + 0.5f) ;
        } else if (reqHeight <= 0) {
            reqHeight = (int) (height * reqWidth / (float)width + 0.5f);
        }

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            if (inSampleSize == 0) {
                inSampleSize = 1;
            }

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger
            // inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down
            // further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }

        return inSampleSize;
    }

}
