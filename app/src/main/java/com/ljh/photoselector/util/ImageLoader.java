package com.ljh.photoselector.util;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ljh.photoselector.R;

/**
 * Created by xuchao on 15-7-9.
 */
public class ImageLoader {
    public static void loadFromLocal(Context context, String path, ImageView imageView) {
        Uri mUri = Uri.parse("file://" + path);
        Glide.with(context).loadFromMediaStore(mUri)
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .centerCrop()
                .into(imageView);
    }

    public static void loadFromLocal(Context context, String path, ImageView imageView,int type) {
        Uri mUri = Uri.parse("file://" + path);
        Glide.with(context).loadFromMediaStore(mUri)
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(imageView);
    }

}
