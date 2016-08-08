package com.ljh.photoselector.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by ljh on 2016/3/16.
 */
public class ExternalStorage {

    /**
     * 外部存储根目录
     */
    private String mSDKStorageRoot = null;

    private static ExternalStorage instance;

    private ExternalStorage() {

    }

    public static ExternalStorage getInstance() {
        if (null == instance) {
            instance = new ExternalStorage();
        }
        return instance;
    }

    public void init(Context context, String sdkStorageRoot) {

        if (!TextUtils.isEmpty(sdkStorageRoot)) {
            File dir = new File(sdkStorageRoot);
            if (!dir.exists()) {
                dir.mkdir();
            }
            if (dir.exists() && !dir.isFile()) {
                this.mSDKStorageRoot = sdkStorageRoot;
                if (!sdkStorageRoot.endsWith("/")) {
                    this.mSDKStorageRoot = sdkStorageRoot + "/";
                }

            }
        }

        if (TextUtils.isEmpty(this.mSDKStorageRoot)) {
            loadStorageState(context);
        }
        createSubFolders();
    }

    private void createSubFolders() {
        boolean result=true;
        File root=new File(mSDKStorageRoot);
        if (root.exists()&&!root.isDirectory()){
            root.delete();
        }
        for (StorageType storageType : StorageType.values()) {
            result &= makeDirectory(mSDKStorageRoot + storageType.getStoragePath());
        }
        if (result) {
            createNoMediaFile(mSDKStorageRoot);
        }
    }

    protected static String NO_MEDIA_FILE_NAME = ".nomedia";

    private void createNoMediaFile(String path) {
        File noMediaFile = new File(path + "/" + NO_MEDIA_FILE_NAME);
        try {
            if (!noMediaFile.exists()) {
                noMediaFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 创建目录
     *
     * @param path
     * @return
     */
    private boolean makeDirectory(String path) {
        File file = new File(path);
        boolean exist = file.exists();
        if (!exist) {
            exist = file.mkdirs();
        }
        return exist;
    }

    private void loadStorageState(Context context) {
        String externalPath= Environment.getExternalStorageDirectory().getPath();
        this.mSDKStorageRoot=externalPath+"/"+context.getPackageName()+"/";
    }

    
    /**
     * 文件全名转绝对路径（写）
     *
     * @param fileName 文件全名（文件名.扩展名）
     * @return 返回绝对路径信息
     */
    public String getWritePath(String fileName, StorageType fileType) {
        return pathForName(fileName, fileType, false, false);
    }

    private String pathForName(String fileName, StorageType fileType, boolean dir,
                               boolean check) {
        String directory = getDirectoryByDirType(fileType);
        StringBuilder path = new StringBuilder(directory);
        if (!dir) {
            path.append(fileName);
        }
        String pathString = path.toString();
        File file = new File(pathString);

        if (check) {
            if (file.exists()) {
                if ((dir && file.isDirectory())
                        || (!dir && !file.isDirectory())) {
                    return pathString;
                }
            }

            return "";
        } else {
            return pathString;
        }
    }

    /**
     * 返回指定类型的文件夹路径
     *
     * @param fileType
     * @return
     */
    public String getDirectoryByDirType(StorageType fileType) {
        return mSDKStorageRoot + fileType.getStoragePath();
    }

}
