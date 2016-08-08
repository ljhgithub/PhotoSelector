package com.ljh.photoselector.ui.media.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ljh.photoselector.R;
import com.ljh.photoselector.constant.Extras;
import com.ljh.photoselector.model.PhotoFolder;
import com.ljh.photoselector.model.PhotoModel;
import com.ljh.photoselector.ui.media.adapter.PickerAlbumAdapter;
import com.ljh.photoselector.ui.media.adapter.PickerImageAdapter;
import com.ljh.photoselector.ui.media.fragment.PickerImageFragment;
import com.ljh.photoselector.util.LogUtils;
import com.ljh.photoselector.util.media.MediaImageProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ljh on 2016/2/29.
 */
public class PickerImageActivity extends AppCompatActivity implements PickerImageAdapter.OnPhotoSelectedListener, PickerImageAdapter.OnPhotoClickListener, View.OnClickListener {
    private static final String TAG = LogUtils.makeLogTag(PickerImageActivity.class.getSimpleName());
    public static final int REQUEST_IMAGE_FROM_LOCAL = 100;
    public static final int selectImagesLimit = 9;

    private RecyclerView rvAlbum;
    private PickerImageFragment pickerImageFragment;
    private PickerAlbumAdapter albumRecyclerViewAdapter;
    private TextView tvAlbumName;
    private BottomSheetBehavior behavior;
    private ArrayList<PhotoModel> selectedImages;
    private Button btnPreview;
    private Button btnSend;
    private ArrayList<PhotoModel> previewPhotos;
    private PhotoFolder currentFolder;
    MediaImageProvider mediaImageProvider;
    private View vCover;

    public static void start(Activity activity) {
        Intent it = new Intent(activity, PickerImageActivity.class);
        activity.startActivityForResult(it, REQUEST_IMAGE_FROM_LOCAL);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker_image);
        selectedImages = new ArrayList<>();
        findViews();
        mediaImageProvider = new MediaImageProvider();
        behavior = BottomSheetBehavior.from(rvAlbum);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
//                LogUtils.LOGD(TAG, "new state" + newState);
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    vCover.setVisibility(View.GONE);
                } else {
                    vCover.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
//                LogUtils.LOGD(TAG, "slideOffset" + slideOffset);
                vCover.setAlpha(slideOffset);
                ViewCompat.setScaleX(bottomSheet, slideOffset);
                ViewCompat.setScaleY(bottomSheet, slideOffset);

            }
        });


        tvAlbumName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
    }

    private void findViews() {
        rvAlbum = (RecyclerView) findViewById(R.id.rv_album);
        tvAlbumName = (TextView) findViewById(R.id.tv_album_name);
        btnPreview = (Button) findViewById(R.id.btn_preview);
        btnSend = (Button) findViewById(R.id.btn_send);

        btnPreview.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        findViewById(R.id.ib_up).setOnClickListener(this);
        vCover = findViewById(R.id.covering);
        vCover.setOnClickListener(this);

    }

    private void onAlbumItemClick(PhotoFolder folders) {
        List<PhotoModel> photoList = folders.photos;
        if (photoList == null) {
            return;
        }
        for (PhotoModel photo : photoList) {
            if (checkSelectImage(photo)) {
                photo.isSelected = true;
            } else {
                photo.isSelected = false;
            }
        }

    }

    private boolean checkSelectImage(PhotoModel photo) {
        boolean isSelect = false;
        for (int i = 0; i < selectedImages.size(); i++) {
            PhotoModel select = selectedImages.get(i);
            if (select.id == photo.id) {
                isSelect = true;
                break;
            }
        }

        return isSelect;
    }

    @Override
    public void onBackPressed() {
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        rvAlbum.setLayoutManager(new LinearLayoutManager(this));
        rvAlbum.setItemAnimator(new DefaultItemAnimator());
        addFragments();


        mediaImageProvider.load(this).setProcessListener(new MediaImageProvider.ProcessListener() {
            @Override
            public void onProcessStart() {
                LogUtils.LOGD(TAG, "onProcessStart ");
            }

            @Override
            public void onProcessCompleted() {
                LogUtils.LOGD(TAG, "onProcessCompleted ");
                initImagesData(mediaImageProvider);
            }
        });

    }

    private void addFragments() {
        pickerImageFragment = new PickerImageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Extras.EXTRA_IMAGE_SELECT_LIMIT, selectImagesLimit);
        pickerImageFragment.setArguments(bundle);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fragment, pickerImageFragment, PickerImageFragment.class.getSimpleName());
        fragmentTransaction.commitAllowingStateLoss();
    }


    @Override
    public void addSelectedPhoto(PhotoModel photo) {
        selectedImages.add(photo);
        btnPreview.setEnabled(true);
        btnSend.setEnabled(true);
        btnPreview.setText("预览(" + selectedImages.size() + ")");
        btnSend.setText("发送(" + selectedImages.size() + "/" + selectImagesLimit + ")");
    }

    @Override
    public void removeSelectedPhoto(PhotoModel photo) {
        Iterator<PhotoModel> lIterator = selectedImages.iterator();
        while (lIterator.hasNext()) {
            PhotoModel select = lIterator.next();
            if (select.id == photo.id) {
                lIterator.remove();
            }
        }
        updateSelectImagesCount(selectedImages.size());

    }

    private void updateSelectImagesCount(int count) {
        if (count == 0) {
            btnPreview.setEnabled(false);
            btnSend.setEnabled(false);
            btnPreview.setText("预览");
            btnSend.setText("发送");
        } else {
            btnPreview.setEnabled(true);
            btnSend.setEnabled(true);
            btnPreview.setText("预览(" + count + ")");
            btnSend.setText("发送(" + count + "/" + selectImagesLimit + ")");
        }
    }

    @Override
    public void selectedPhotoUpLimit(int limit) {
        Toast.makeText(this, "最多只能选" + limit + "张", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPhotoClick(PhotoModel photo, int position) {
//        mediaImageLoader.folderMap.get(photo.origin)
        LogUtils.LOGD(TAG, "onPhotoClick" + position);
        if (null != currentFolder) {
            previewPhotos = currentFolder.photos;
            PreviewImageFromLocalActivity.start(this, previewPhotos, selectImagesLimit, selectedImages.size(), position);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_preview:
                previewPhotos = selectedImages;
                PreviewImageFromLocalActivity.start(this, previewPhotos, selectImagesLimit, selectedImages.size(), 0);
                break;
            case R.id.btn_send:
                sendImages();
                break;
            case R.id.ib_up:
                onBackPressed();
                break;
            case R.id.covering:
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case PreviewImageFromLocalActivity.REQUEST_PREVIEW_IMAGE_FROM_LOCAL:
                    int backType = data.getIntExtra(Extras.EXTRA_BACK_TYPE, 0);
                    ArrayList<PhotoModel> changePhotos = data.getParcelableArrayListExtra(Extras.EXTRA_IMAGE_CHANGE_LIST);
                    for (PhotoModel change : changePhotos) {
                        for (PhotoModel preview : previewPhotos) {
                            if (change.id == preview.id) {
                                if (preview.isSelected != change.isSelected) {
                                    preview.isSelected = change.isSelected;
                                }
                                LogUtils.LOGD(TAG, "preview" + preview.isSelected);
                            }
                        }
                        Iterator<PhotoModel> iterator = selectedImages.iterator();
                        boolean isContain = false;
                        while (iterator.hasNext()) {
                            PhotoModel photo = iterator.next();
                            if (change.id == photo.id) {
                                isContain = true;
                                if (!change.isSelected) {
                                    iterator.remove();
                                }
                                break;
                            }

                        }
                        if (!isContain && change.isSelected) {
                            selectedImages.add(change);
                        }

                    }
                    if (PreviewImageFromLocalActivity.BACK_TYPE_SEND == backType) {
                        sendImages();
                    } else {
                        pickerImageFragment.resetFragment(currentFolder.photos, selectedImages.size());
                        updateSelectImagesCount(selectedImages.size());
                    }

                    break;

                default:
                    break;

            }
        }
    }

    private void sendImages() {
        Intent resultIt = new Intent();
        resultIt.putParcelableArrayListExtra(Extras.EXTRA_IMAGE_LISTS, selectedImages);
        setResult(RESULT_OK, resultIt);
        finish();
    }

    private void initImagesData(MediaImageProvider provider) {
        if (null != provider.allFolders && provider.allFolders.size() > 0) {
            currentFolder = provider.allFolders.get(0);
        }
        albumRecyclerViewAdapter = new PickerAlbumAdapter(PickerImageActivity.this, provider.allFolders);
        rvAlbum.setAdapter(albumRecyclerViewAdapter);
        pickerImageFragment.resetFragment(provider.allPhotos, selectedImages.size());
        albumRecyclerViewAdapter.setOnAlbumClickListener(new PickerAlbumAdapter.OnAlbumClickListener() {
            @Override
            public void onAlbumClick(PhotoFolder folder) {
                currentFolder = folder;
                onAlbumItemClick(folder);
                pickerImageFragment.resetFragment(folder.photos, selectedImages.size());
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                LogUtils.LOGD(TAG, "photos " + folder.photos.size());
            }
        });
    }

}
