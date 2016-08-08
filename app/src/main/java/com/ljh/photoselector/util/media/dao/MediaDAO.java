package com.ljh.photoselector.util.media.dao;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.ljh.photoselector.util.LogUtils;

/**
 * Created by ljh on 2016/8/4.
 */
public class MediaDAO {
    public static final String TAG = LogUtils.makeLogTag(MediaDAO.class.getSimpleName());

    public static Cursor getAllMediaImageThumbnails(final Context context) {
        final String[] projection = new String[]{
                MediaStore.Images.Thumbnails._ID,
                MediaStore.Images.Thumbnails.IMAGE_ID,
                MediaStore.Images.Thumbnails.DATA};
        final Uri images = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(images, projection, null,
                    null, MediaStore.Images.Thumbnails._ID + " DESC");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.LOGE(TAG, "getAllMediaImageThumbnails exception: " + e.getMessage());
        }
        return cursor;
    }

    public static Cursor getAllMediaImages(final Context context) {
        final String[] projection = new String[] {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATA };
        final Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(images, projection, null,
                    null, MediaStore.Images.Media.DATE_MODIFIED + " DESC");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.LOGE(TAG, "getAllMediaImages exception: " + e.getMessage());
        }

        return cursor;
    }
}
