package com.ljh.photoselector.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by xuchao on 15-6-15.
 */
public class PhotoFolder implements Parcelable {

    /**
     * 图片的文件夹路径
     */
    public String path;

    /**
     * 第一张图片的路径
     */
    public String firstImagePath;

    /**
     * 第一张图片的Id
     */
    public int firstImageID;
    /**
     * 文件夹的名称
     */
    public String name;


    public ArrayList<PhotoModel> photos;

    public PhotoFolder() {
    }

    public PhotoFolder(Parcel in) {
        path = in.readString();
        firstImagePath = in.readString();
        firstImageID = in.readInt();
        name = in.readString();
        photos = in.readArrayList(PhotoModel.class.getClassLoader());
    }

    public int getCount() {
        return photos.size();
    }

    public static final Parcelable.Creator<PhotoFolder> CREATOR = new Creator<PhotoFolder>() {
        @Override
        public PhotoFolder createFromParcel(Parcel source) {
            return new PhotoFolder(source);
        }

        @Override
        public PhotoFolder[] newArray(int size) {
            return new PhotoFolder[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(firstImagePath);
        dest.writeInt(firstImageID);
        dest.writeString(name);
        dest.writeList(photos);
    }
}
