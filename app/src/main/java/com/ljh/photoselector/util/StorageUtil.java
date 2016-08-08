package com.ljh.photoselector.util;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by ljh on 2016/3/10.
 */
public class StorageUtil {
    public final static long K = 1024;
    public final static long M = 1024 * 1024;
    // 外置存储卡默认预警临界值
    private static final long THRESHOLD_WARNING_SPACE = 100 * M;
    // 保存文件时所需的最小空间的默认值
    public static final long THRESHOLD_MIN_SPCAE = 20 * M;

    public static void init(Context context, String rootPath) {
        ExternalStorage.getInstance().init(context, rootPath);

    }

    /**
     * 获取文件保存路径，没有toast提示
     *
     * @param fileName
     * @param fileType
     * @return 可用的保存路径或者null
     */
    public static String getWritePath(String fileName, StorageType fileType) {
        return getWritePath(fileName, fileType, false);
    }

    /**
     * 获取文件保存路径
     *
     * @param fileName 文件全名
     * @param tip      空间不足时是否给出默认的toast提示
     * @return 可用的保存路径或者null
     */
    private static String getWritePath(String fileName, StorageType fileType, boolean tip) {
        String path = ExternalStorage.getInstance().getWritePath(fileName, fileType);
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        File dir = new File(path).getParentFile();
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
        return path;
    }
}
