package com.ljh.photoselector.util.media;

import android.content.Context;
import android.graphics.Bitmap;

import com.ljh.photoselector.ui.MainActivity;
import com.ljh.photoselector.util.AttachmentStore;
import com.ljh.photoselector.util.FileUtil;
import com.ljh.photoselector.util.LogUtils;
import com.ljh.photoselector.util.ScreenUtil;
import com.ljh.photoselector.util.StorageType;
import com.ljh.photoselector.util.StorageUtil;
import com.ljh.photoselector.util.StringUtil;

import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.BoringLayout;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ljh on 2016/3/16.
 */
public class ImageUtil {
    public static final String TAG = LogUtils.makeLogTag(ImageUtil.class.getSimpleName());

    public static class ImageSize {
        public int width = 0;
        public int height = 0;

        public ImageSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    public static File getScaledImageFile(File imageFile, String mimeType) {

        String filePath = imageFile.getPath();
        if (!isInvalidPictureFile(mimeType)) {
            LogUtils.LOGI(TAG, "is invalid picture file");
            return null;
        }
        String tempFilePath = getTempFilePath(FileUtil.getExtensionName(filePath));
        File tempImageFile = AttachmentStore.create(tempFilePath);
        LogUtils.LOGI(TAG, "tempImageFile"+tempImageFile.getPath());
        if (null == tempImageFile) {
            return null;
        }
        CompressFormat compressFormat = CompressFormat.JPEG;
        // 压缩数值由第三方开发者自行决定
        int maxWidth = 720;
        int quality = 60;
        if (ImageUtil.scaleImage(imageFile, tempImageFile, maxWidth, compressFormat, quality)) {
            return tempImageFile;
        } else {
            return null;
        }
    }

    private static boolean scaleImage(File srcFile, File dstFile, int dstMaxWH, CompressFormat compressFormat, int quality) {
        boolean success = false;
        try {


            int inSampleSize = SampleSizeUtil.calculateSampleSize(srcFile.getAbsolutePath(), dstMaxWH * dstMaxWH);
            Bitmap srcBitmap = BitmapDecoder.decodeSampled(srcFile.getPath(), inSampleSize);
            LogUtils.LOGD(TAG, "srcBitmap " + srcBitmap.getWidth() + "  " + srcBitmap.getHeight());
            if (null == srcBitmap) {
                return success;
            }
            //旋转
            ExifInterface localExifInterface = new ExifInterface(srcFile.getAbsolutePath());
            int rotateInt = localExifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            float rotate = getImageRotate(rotateInt);
            float scale = (float) Math.sqrt((float) (dstMaxWH * dstMaxWH) / (float) (srcBitmap.getWidth() * srcBitmap.getHeight()));
            LogUtils.LOGD(TAG, "rotate:  " + rotate);
            LogUtils.LOGD(TAG, "scale:  " + scale);
            Bitmap dstBitmap;
            if (rotate == 0 && scale >= 1) {
                dstBitmap = srcBitmap;
            } else {
                try {
                    Matrix matrix = new Matrix();
                    if (rotate != 0) {
                        matrix.preRotate(rotate);
                    }
                    if (scale < 1) {
                        matrix.postScale(scale, scale);
                    }
                    dstBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
                    LogUtils.LOGD(TAG, "dstBitmap " + dstBitmap.getWidth() + "  " + dstBitmap.getHeight());

                } catch (OutOfMemoryError e) {
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dstFile));
                    srcBitmap.compress(compressFormat, quality, bos);
                    bos.flush();
                    bos.close();
                    success = true;
                    if (!srcBitmap.isRecycled())
                        srcBitmap.recycle();
                    srcBitmap = null;
                    return success;
                }

                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dstFile));
                dstBitmap.compress(compressFormat, quality, bos);
                bos.flush();
                bos.close();
                success = true;

                if (!srcBitmap.isRecycled())
                    srcBitmap.recycle();
                srcBitmap = null;

                if (!dstBitmap.isRecycled())
                    dstBitmap.recycle();
                dstBitmap = null;
            }
            LogUtils.LOGD(TAG, "dstBitmap 2 " + dstBitmap.getWidth() + "  " + dstBitmap.getHeight());
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return success;
    }

    private static float getImageRotate(int rotateInt) {
        float f;
        switch (rotateInt) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                f = 90.0F;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                f = 180.0F;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                f = 270.0F;
                break;
            default:
                f = 0.0F;
                break;
        }
        return f;
    }

    private static String getTempFilePath(String extension) {

        return StorageUtil.getWritePath("temp_image_" + StringUtil.get32UUID() + "." + extension, StorageType.TYPE_TEMP);
    }

    public static boolean isInvalidPictureFile(String mimeType) {
        String lowerCaseFilepath = mimeType.toLowerCase();
        return (lowerCaseFilepath.contains("jpg") || lowerCaseFilepath.contains("jpeg")
                || lowerCaseFilepath.toLowerCase().contains("png") || lowerCaseFilepath.toLowerCase().contains("bmp") || lowerCaseFilepath
                .toLowerCase().contains("gif"));
    }

    public static String makeThumbnail(Context context, File imageFile) {
        String thumbFilePath = StorageUtil.getWritePath(imageFile.getName(), StorageType.TYPE_THUMB_IMAGE);
        File thumbFile = AttachmentStore.create(thumbFilePath);
        if (null == thumbFile) {
            return null;
        }
        boolean result = scaleThumbnail(imageFile, thumbFile, getImageMaxEdge(), getImageMinEdge(), CompressFormat.JPEG,
                60);
        if (!result) {
            AttachmentStore.delete(thumbFilePath);
            return null;
        }

        return thumbFilePath;
    }

    private static boolean scaleThumbnail(File srcFile, File dstFile, int dstMaxWH, int dstMinWH, CompressFormat compressFormat, int quality) {
        Boolean success = false;
        Bitmap srcBitmap = null;
        Bitmap dstBitmap = null;
        BufferedOutputStream bos = null;
        try {
            int[] bound = BitmapDecoder.decodeBound(srcFile);
            ImageSize imageSize = getThumbnailDisplaySize(bound[0], bound[1], dstMaxWH, dstMinWH);
            srcBitmap = BitmapDecoder.decodeSampled(srcFile.getPath(), imageSize.width, imageSize.height);
            ExifInterface localExifInterface = new ExifInterface(srcFile.getAbsolutePath());
            int rotateInt = localExifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            float rotate = getImageRotate(rotateInt);
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            float inSampleSize = 1;
            LogUtils.LOGD(TAG, "srcBitmap width  " + srcBitmap.getWidth() + " height " + srcBitmap.getHeight() + " request width " + imageSize.width + " height " + imageSize.height);
            if (srcBitmap.getWidth() > dstMinWH && srcBitmap.getWidth() <= dstMaxWH && srcBitmap.getHeight() >= dstMinWH && srcBitmap.getHeight() <= dstMaxWH) {
                //如果第一轮拿到的srcBitmap尺寸都符合要求，不需要再做缩放
            } else {
                if (srcBitmap.getWidth() != imageSize.width || srcBitmap.getHeight() != imageSize.height) {
                    float widthScale = (float) imageSize.width / (float) srcBitmap.getWidth();
                    float heightScale = (float) imageSize.height / (float) srcBitmap.getHeight();
                    if (widthScale >= heightScale) {
                        imageSize.width = srcBitmap.getWidth();
                        imageSize.height /= widthScale;//必定小于srcBitmap.getHeight()
                        inSampleSize = widthScale;
                    } else {
                        imageSize.width /= heightScale;//必定小于srcBitmap.getWidth()
                        imageSize.height = srcBitmap.getHeight();
                        inSampleSize = heightScale;
                    }
                }
            }
            matrix.postScale(inSampleSize, inSampleSize);
            if (rotate == 0 && inSampleSize == 1) {
                dstBitmap = srcBitmap;
            } else {
                dstBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, imageSize.width, imageSize.height, matrix, true);
            }

            bos = new BufferedOutputStream(new FileOutputStream(dstFile));
            dstBitmap.compress(compressFormat, quality, bos);
            bos.flush();
            success = true;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (srcBitmap != null && !srcBitmap.isRecycled()) {
                srcBitmap.recycle();
                srcBitmap = null;
            }

            if (dstBitmap != null && !dstBitmap.isRecycled()) {
                dstBitmap.recycle();
                dstBitmap = null;
            }
        }
        return success;
    }
    private static ImageSize getThumbnailDisplaySize(float srcWidth, float srcHeight, float dstMaxWH, float dstMinWH) {
        if (srcWidth <= 0 || srcHeight <= 0) { // bounds check
            return new ImageSize((int) dstMinWH, (int) dstMinWH);
        }
        float shorter;
        float longer;
        boolean widthIsShorter;
        if (srcHeight < srcWidth) {
            shorter = srcHeight;
            longer = srcWidth;
            widthIsShorter = false;
        } else {
            shorter = srcWidth;
            longer = srcHeight;
            widthIsShorter = true;
        }

        if (shorter < dstMinWH) {
            float scale = shorter / dstMinWH;
            shorter = dstMinWH;
            if (longer * scale > dstMaxWH) {
                longer = dstMaxWH;
            } else {
                longer *= scale;
            }
        } else if (longer > dstMaxWH) {
            float scale = dstMaxWH / longer;
            longer = dstMaxWH;
            if (shorter * scale < dstMinWH) {
                shorter = dstMinWH;
            } else {
                shorter *= scale;
            }
        }
        if (widthIsShorter) {
            srcWidth = shorter;
            srcHeight = longer;
        } else {
            srcWidth = longer;
            srcHeight = shorter;
        }
        return new ImageSize((int) srcWidth, (int) srcHeight);

    }

    public static int getImageMaxEdge() {
        return (int) (165.0 / 320.0 * ScreenUtil.screenWidth);
    }

    public static int getImageMinEdge() {
        return (int) (76.0 / 320.0 * ScreenUtil.screenWidth);
    }
}
