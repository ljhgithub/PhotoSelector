package com.ljh.photoselector.ui.media.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ljh.photoselector.R;
import com.ljh.photoselector.constant.Extras;
import com.ljh.photoselector.model.PhotoModel;
import com.ljh.photoselector.ui.media.adapter.PickerImageAdapter;
import com.ljh.photoselector.ui.media.activity.PickerImageActivity;
import com.ljh.photoselector.util.LogUtils;

import java.util.ArrayList;

/**
 * Created by ljh on 2016/2/29.
 */
public class PickerImageFragment extends Fragment {
    private static final String TAG = LogUtils.makeLogTag(PickerImageFragment.class.getSimpleName());
    private RecyclerView rvImage;
    private GridLayoutManager glm;
    private PickerImageAdapter pickerPhotoAdapter;
    private  ArrayList<PhotoModel> mPhotos;
    private int selectedCount, selectedPhotoCountLimit;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_picker_image, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        parseExtra();
    }

    private void findViews(View view) {
        rvImage = (RecyclerView) view.findViewById(R.id.rv_image);
        glm = new GridLayoutManager(getContext(), 3);
        rvImage.setLayoutManager(glm);
    }


    public void resetFragment(ArrayList<PhotoModel> list,int selectCount) {
        this.selectedCount =selectCount;
        if (mPhotos == null) {
            mPhotos = new ArrayList<>();
        } else {
            mPhotos.clear();
        }
        if (list != null) {
            mPhotos.addAll( list);
        }
        pickerPhotoAdapter = new PickerImageAdapter(getContext());
        pickerPhotoAdapter.setData(mPhotos,selectCount, selectedPhotoCountLimit);
        rvImage.setAdapter(pickerPhotoAdapter);
        pickerPhotoAdapter.setOnPhotoSelectedListener((PickerImageActivity) getActivity());
        pickerPhotoAdapter.setOnPhotoClickListener((PickerImageActivity)getActivity());
    }


    private void parseExtra() {
        Bundle bundle = getArguments();
        selectedPhotoCountLimit =bundle.getInt(Extras.EXTRA_IMAGE_SELECT_LIMIT);
//        mPhotos = new ArrayList<>();
        mPhotos = bundle.getParcelableArrayList(Extras.EXTRA_IMAGE_LISTS);
    }
}
