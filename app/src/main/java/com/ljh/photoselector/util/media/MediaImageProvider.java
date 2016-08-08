package com.ljh.photoselector.util.media;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.ljh.photoselector.model.PhotoFolder;
import com.ljh.photoselector.model.PhotoModel;
import com.ljh.photoselector.util.LogUtils;
import com.ljh.photoselector.util.media.dao.MediaDAO;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by ljh on 2016/8/4.
 */
public class MediaImageProvider {

    public static final String TAG = LogUtils.makeLogTag(MediaImageProvider.class.getSimpleName());

    private ProcessListener processListener;


    /**
     * 图集map
     * 图集名作为map的key
     */
    public final HashMap<String, PhotoFolder> folderMap;
    /**
     * 所有图片
     */
    public final ArrayList<PhotoModel> allPhotos;
    /**
     * 所有图集
     */
    public final ArrayList<PhotoFolder> allFolders;


    public MediaImageProvider() {
        folderMap = new HashMap<>();
        allPhotos = new ArrayList<>();
        allFolders = new ArrayList<>();
    }

    public MediaImageProvider load(Context context) {
        Observable.just(context)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (null != processListener) {
                            processListener.onProcessStart();
                        }
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Context, String>() {
                    @Override
                    public String call(Context context) {
                        getMediaImages(context);
                        getMediaThumbnails(context);
                        return "completed";
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String o) {
                        if (null != processListener) {
                            processListener.onProcessCompleted();
                        }
                    }
                });
        return this;
    }

    public MediaImageProvider setProcessListener(ProcessListener processListener) {
        this.processListener = processListener;
        return this;
    }


    /**
     * 获取缩略图
     */
    private void getMediaThumbnails(Context context) {
        Cursor cursor = null;
        try {
            cursor = MediaDAO.getAllMediaImageThumbnails(context);
            if (null != cursor && cursor.moveToFirst()) {
                int imageId;
                String thumbnailPath;
                do {
                    imageId = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID));
                    thumbnailPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
                    MediaImageThumbnailsCache.put(imageId, thumbnailPath);
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取原图
     * 将所有图片根据图集分类
     */
    private void getMediaImages(Context context) {
        Cursor cursor = null;
        allFolders.clear();
        allPhotos.clear();
        folderMap.clear();
        try {
            cursor = MediaDAO.getAllMediaImages(context);
            if (null != cursor && cursor.moveToFirst()) {
                PhotoFolder folderInfo;
                PhotoModel photoInfo;
                ArrayList<PhotoModel> photos;
                int index = 0;
                int id;
                String path;
                String bucket;
                long size;
                do {
                    id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                    path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    bucket = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                    size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                    if (!isValidImageFile(path)) {
                        LogUtils.LOGD(TAG, "it is not a vaild path:" + path);
                        continue;
                    }
//                    LogUtils.LOGD(TAG, "bucket:" + bucket+path);
                    photoInfo = new PhotoModel();
                    photoInfo.id = id;
                    photoInfo.origin = path;
                    photoInfo.size = size;
                    allPhotos.add(photoInfo);
                    if (folderMap.containsKey(bucket)) {
                        PhotoFolder folder = folderMap.remove(bucket);
                        folder.photos.add(photoInfo);
                        folderMap.put(bucket, folder);
                        if (allFolders.contains(folder)) {
                            index = allFolders.indexOf(folder);
                            allFolders.set(index, folder);
                        } else {
                            allFolders.add(folder);
                        }


                    } else {
                        folderInfo = new PhotoFolder();
                        photos = new ArrayList<>();
                        photos.add(photoInfo);
                        folderInfo.photos = photos;
                        folderInfo.firstImageID = id;
                        folderInfo.firstImagePath = path;
                        folderInfo.name = bucket;
                        folderMap.put(bucket, folderInfo);
                        allFolders.add(folderInfo);
                    }
                } while (cursor.moveToNext());

                PhotoFolder folder = new PhotoFolder();
                folder.photos = allPhotos;
                folder.name = "全部";
                folder.firstImageID = allPhotos.get(0).id;
                folder.firstImagePath = allPhotos.get(0).origin;
                folderMap.put(folder.name, folder);
                allFolders.add(0, folder);

            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.LOGD(TAG, "Exception:" + e.getMessage());
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isValidImageFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }

        File imageFile = new File(filePath);
        if (imageFile.exists()) {
            return true;
        }

        return false;
    }

    public interface ProcessListener {
        void onProcessStart();

        void onProcessCompleted();
    }
}
