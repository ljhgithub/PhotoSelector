package com.ljh.photoselector.util;

/**
 * Created by ljh on 2016/3/16.
 */
public class FileUtil {

    // 获取文件扩展名
    public static String getExtensionName(String filename) {
        if ((null != filename) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return "";
    }
}
