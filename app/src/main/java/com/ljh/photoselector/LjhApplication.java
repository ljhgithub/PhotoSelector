package com.ljh.photoselector;

import android.app.Application;

/**
 * Created by ljh on 2016/3/18.
 */
public class LjhApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LjhUIKit.init(this);
    }
}
