package com.ljh.photoselector.util;

import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by ljh on 2016/3/16.
 */
public class AttachmentStore {
    public static final String TAG=LogUtils.makeLogTag(AttachmentStore.class.getSimpleName());
    /**
     * 删除指定路径文件
     *
     * @param path
     * @return
     */
    public static boolean delete(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File f = new File(path);
        if (f.exists()) {
            f = renameOnDelete(f);
            return f.delete();
        } else {
            return false;
        }
    }

    private static File renameOnDelete(File file) {
        File tempFile = new File(file.getParent() + "/" + System.currentTimeMillis() + "_tmp");
        if (file.renameTo(tempFile)) {
            return tempFile;
        }
        return file;
    }

    public static File create(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        File f = new File(filePath);
        if (!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }

        try {
            f.createNewFile();
            return f;
        } catch (IOException e) {
            if (null != f && f.exists()) {
                f.delete();
            }
            return null;
        }
    }
}
