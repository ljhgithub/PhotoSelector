package com.ljh.photoselector.ui.media.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ljh.photoselector.R;
import com.ljh.photoselector.model.PhotoModel;
import com.ljh.photoselector.ui.media.adapter.PreviewImageFromLocalPagerAdapter;
import com.ljh.photoselector.ui.view.HackyViewPager;

import java.util.ArrayList;

/**
 * Created by ljh on 2016/3/3.
 */
public class PreviewImageFromLocalFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private HackyViewPager kvpPreviewPhoto;
    private PreviewImageFromLocalPagerAdapter adapter;
    private ArrayList<PhotoModel> mPhotos;
    private OnImageChangeListener imageChangeListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preview_image_from_local, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        kvpPreviewPhoto = (HackyViewPager) view.findViewById(R.id.hvp_photo);
        kvpPreviewPhoto.addOnPageChangeListener(this);

    }

    public void resetFragment(ArrayList<PhotoModel> photos, int currentPageIndex) {
        if (mPhotos == null) {
            mPhotos = new ArrayList<>();
        } else {
            mPhotos.clear();
        }
        if (photos != null) {
            mPhotos.addAll(photos);
        }
        adapter = new PreviewImageFromLocalPagerAdapter(getActivity());
        adapter.setData(mPhotos);
        kvpPreviewPhoto.setAdapter(adapter);
        kvpPreviewPhoto.setCurrentItem(currentPageIndex);
    }

    public void setOnImageChangeListener(OnImageChangeListener listener) {
        if (null != listener) {
            imageChangeListener = listener;
        }

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        if (null != imageChangeListener) {
            imageChangeListener.onImageChanged(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public interface OnImageChangeListener {
        void onImageChanged(int index);
    }
}
