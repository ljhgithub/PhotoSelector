package com.ljh.photoselector.util.media;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ljh on 2016/3/16.
 */
public class BitmapDecoder {
    public static int[] decodeBound(String pathName) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName,options);
        return new int[]{options.outWidth, options.outHeight};
    }

    public static int[] decodeBound(File file) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            int[] bound = decodeBound(is);
            return bound;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return new int[]{0, 0};
    }

    public static int[] decodeBound(InputStream is) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, options);

        return new int[]{options.outWidth, options.outHeight};
    }

    public static Bitmap decodeSampled(String pathName, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = sampleSize;
        Bitmap b = null;
        try {
            b = BitmapFactory.decodeFile(pathName, options);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }

        return checkInBitmap(b, options, pathName);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static Bitmap checkInBitmap(Bitmap bitmap, BitmapFactory.Options options, String pathName) {
        boolean honeycomb = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
        if (honeycomb && bitmap != options.inBitmap && null != options.inBitmap) {
            options.inBitmap.recycle();
            options.inBitmap = null;
        }
        if (null == bitmap) {
            try {
                bitmap = BitmapFactory.decodeFile(pathName, options);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }

        }
        return bitmap;
    }

    public static Bitmap decodeSampled(String pathName, int reqWidth, int reqHeight) {

        return decodeSampled(pathName,getSampleSize(pathName,reqWidth,reqHeight));
    }

    public static int getSampleSize(String pathName, int reqWidth, int reqHeight) {
        // decode bound
        int[] bound = decodeBound(pathName);

        // calculate sample size
        int sampleSize = SampleSizeUtil.calculateSampleSize(bound[0], bound[1], reqWidth, reqHeight);

        return sampleSize;
    }
}
