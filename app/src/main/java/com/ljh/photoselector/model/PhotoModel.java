package com.ljh.photoselector.model;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by xuchao on 15-7-9.
 */
public class PhotoModel implements PhotoUrlModel, Parcelable, Comparable<PhotoModel> {

    public static final int THUMBNAIL = 1;
    public static final int ORIGIN = 2;
    public static final int NORMAL = 3;

    public int id;
    public String thumbnail;
    public String origin;
    public String normal;
    public long addDate;
    public boolean isSelected;
    public long size;


    public PhotoModel() {
    }

    public static final Parcelable.Creator<PhotoModel> CREATOR = new Creator<PhotoModel>() {
        @Override
        public PhotoModel createFromParcel(Parcel source) {
            return new PhotoModel(source);
        }

        @Override
        public PhotoModel[] newArray(int size) {
            return new PhotoModel[size];
        }
    };

    public PhotoModel(Parcel in) {
        id=in.readInt();
        thumbnail = in.readString();
        origin = in.readString();
        normal = in.readString();
        addDate = in.readLong();
        isSelected =in.readByte()!=0;
        size=in.readLong();
    }

    @Override
    public String buildUrl(int width, int height) {
        String url = "";
        int size=2;//根据 width 和 height 设置size
        switch (size) {
            default:
            case ORIGIN:
                url = origin;
                break;
            case NORMAL:
                url = normal;
                break;
            case THUMBNAIL:
                url = thumbnail;
                break;
        }
        return null == url || url.isEmpty() ? origin : url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(thumbnail);
        dest.writeString(origin);
        dest.writeString(normal);
        dest.writeLong(addDate);
        dest.writeByte((byte)(isSelected ?1:0));
        dest.writeLong(size);
    }

    @Override
    public int compareTo(PhotoModel another) {
        return (int) (this.addDate - another.addDate);
    }
}
