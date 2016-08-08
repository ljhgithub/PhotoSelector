package com.ljh.photoselector.ui.media.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.ljh.photoselector.util.ImageLoader;
import com.ljh.photoselector.model.PhotoModel;
import com.ljh.photoselector.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;


/**
 * Created by xuchao on 15-7-22.
 */
public class PreviewImageFromLocalPagerAdapter extends PagerAdapter {

    private static final String TAG= LogUtils.makeLogTag(PreviewImageFromLocalPagerAdapter.class.getSimpleName());
    private List<PhotoModel> photos;
    private Activity mActivity;

    public PreviewImageFromLocalPagerAdapter(Activity activity) {
        photos = new ArrayList<>();
        mActivity = activity;
    }

    public void setData(List<PhotoModel> data) {
        if (null != data) {
            photos = data;
            notifyDataSetChanged();
        }

    }

    @Override
    public int getCount() {
        return photos.size();
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView;
        photoView = new PhotoView(container.getContext());
        ImageLoader.loadFromLocal(mActivity, photos.get(position).origin, photoView, 0);
        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return photoView;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
