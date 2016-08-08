package com.ljh.photoselector.util.media;

import android.util.SparseArray;

import java.io.File;

/**
 * Created by ljh on 2016/8/5.
 */
public class MediaImageThumbnailsCache {
    private static final SparseArray<String> thumbnailCache = new SparseArray<>();

    public static void put(Integer id, String path) {
        thumbnailCache.put(id, path);
    }

    public static void clear() {
        thumbnailCache.clear();
    }

    public static String getThumbnailPathByImageId(Integer id, String defaultPath) {
        String path = thumbnailCache.get(id, defaultPath);
        File fileThumb = new File(path);
        if (fileThumb.exists()) {
            return path;
        } else {
            return defaultPath;
        }
    }

}
