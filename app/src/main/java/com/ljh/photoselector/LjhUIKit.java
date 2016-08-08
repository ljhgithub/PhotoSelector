package com.ljh.photoselector;

import android.content.Context;

/**
 * Created by ljh on 2016/3/18.
 */
public class LjhUIKit {
    // context
    private static Context mContext;

    public static void init(Context context){

        LjhUIKit.mContext=context;
    }

    public static Context getContext() {
        return mContext;
    }
}
