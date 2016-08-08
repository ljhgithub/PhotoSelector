package com.ljh.photoselector.ui.media.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ljh.photoselector.R;
import com.ljh.photoselector.constant.Extras;
import com.ljh.photoselector.model.PhotoModel;
import com.ljh.photoselector.ui.media.fragment.PreviewImageFromLocalFragment;
import com.ljh.photoselector.util.LogUtils;

import java.util.ArrayList;

/**
 * Created by ljh on 2016/3/7.
 */
public class PreviewImageFromLocalActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = LogUtils.makeLogTag(PreviewImageFromLocalActivity.class.getSimpleName());
    public static final int REQUEST_PREVIEW_IMAGE_FROM_LOCAL = 100;
    public static final int BACK_TYPE_NORMAL = 10;
    public static final int BACK_TYPE_SEND = 1;
    private PreviewImageFromLocalFragment previewImageFromLocalFragment;

    private ImageView ivSelect;
    private TextView tvSelect;
    private Button btnSend;
    private ArrayList<PhotoModel> mPhotos;
    private int imagesSelectLimit;
    private int imagesSelectCount;
    private PhotoModel currentPhoto;
    private SparseArray<PhotoModel> changePhotos;
    private TextView tvTitle;
    private int totalPages;
    private int currentPageIndex;


    public static void start(AppCompatActivity activity, ArrayList<PhotoModel> photos, int selectLimit, int selectCount, int currentImageIndex) {
        if (null == photos || photos.size() == 0) return;
        Intent it = new Intent(activity, PreviewImageFromLocalActivity.class);
        it.putParcelableArrayListExtra(Extras.EXTRA_IMAGE_LISTS, photos);
        it.putExtra(Extras.EXTRA_IMAGE_SELECT_LIMIT, selectLimit);
        it.putExtra(Extras.EXTRA_IMAGE_SELECT_COUNT, selectCount);
        it.putExtra(Extras.EXTRA_IMAGE_CURRENT_INDEX, currentImageIndex);
        activity.startActivityForResult(it, REQUEST_PREVIEW_IMAGE_FROM_LOCAL);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_image_from_local);
        tvSelect = (TextView) findViewById(R.id.tv_select);
        btnSend = (Button) findViewById(R.id.btn_send);
        ivSelect = (ImageView) findViewById(R.id.iv_select);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        findViewById(R.id.ib_up).setOnClickListener(this);
        addFragment();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        btnSend.setOnClickListener(this);
        tvSelect.setOnClickListener(this);
        changePhotos = new SparseArray<>();
        parseExtras();
        previewImageFromLocalFragment.resetFragment(mPhotos, currentPageIndex);
        previewImageFromLocalFragment.setOnImageChangeListener(new PreviewImageFromLocalFragment.OnImageChangeListener() {
            @Override
            public void onImageChanged(int index) {
                currentPageIndex = index;
                currentPhoto = mPhotos.get(index);
                ivSelect.setImageResource(currentPhoto.isSelected ? R.mipmap.picker_image_selected : R.mipmap.picker_image_normal);
                tvTitle.setText(currentPageIndex + 1 + "/" + totalPages);
            }

        });
        tvTitle.setText(currentPageIndex + 1 + "/" + totalPages);
    }

    private void parseExtras() {
        Intent dataIt = getIntent();
        mPhotos = dataIt.getParcelableArrayListExtra(Extras.EXTRA_IMAGE_LISTS);
        imagesSelectLimit = dataIt.getIntExtra(Extras.EXTRA_IMAGE_SELECT_LIMIT, 0);
        imagesSelectCount = dataIt.getIntExtra(Extras.EXTRA_IMAGE_SELECT_COUNT, 0);
        currentPageIndex = dataIt.getIntExtra(Extras.EXTRA_IMAGE_CURRENT_INDEX, 0);
        if (null != mPhotos & mPhotos.size() > currentPageIndex) {
            currentPhoto = mPhotos.get(currentPageIndex);
            ivSelect.setImageResource(currentPhoto.isSelected ? R.mipmap.picker_image_selected : R.mipmap.picker_image_normal);
            updateSelectImageCount(imagesSelectCount);
            totalPages = mPhotos.size();

        }
    }

    private void addFragment() {
        previewImageFromLocalFragment = new PreviewImageFromLocalFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment, previewImageFromLocalFragment, PreviewImageFromLocalFragment.class.getSimpleName());
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_select:
                if (null != currentPhoto) {
                    currentPhoto.isSelected = !currentPhoto.isSelected;
                    if (currentPhoto.isSelected) {
                        if (imagesSelectCount >= imagesSelectLimit) {
                            currentPhoto.isSelected = false;
                            Toast.makeText(this, "最多只能选" + imagesSelectLimit + "张", Toast.LENGTH_SHORT).show();
                        } else {
                            imagesSelectCount++;
                            changePhotos.put(currentPageIndex, currentPhoto);
                            ivSelect.setImageResource(R.mipmap.picker_image_selected);
                        }
                    } else {
                        imagesSelectCount--;
                        changePhotos.put(currentPageIndex, currentPhoto);
                        ivSelect.setImageResource(R.mipmap.picker_image_normal);
                    }
                }
                updateSelectImageCount(imagesSelectCount);
                break;
            case R.id.btn_send:
                goBack(BACK_TYPE_SEND);
                break;
            case R.id.ib_up:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    private void updateSelectImageCount(int count) {
        if (count <= 0) {
            btnSend.setText("发送");
            btnSend.setEnabled(false);
        } else {
            btnSend.setEnabled(true);
            btnSend.setText("发送(" + count + "/" + imagesSelectLimit + ")");
        }
    }

    @Override
    public void onBackPressed() {
        goBack(BACK_TYPE_NORMAL);
    }

    private void goBack(int type) {
        ArrayList<PhotoModel> changePhoto = new ArrayList<>();
        for (int i = 0; i < changePhotos.size(); i++) {
            changePhoto.add(changePhotos.valueAt(i));
        }
        Intent resultIt = new Intent();
        resultIt.putExtra(Extras.EXTRA_BACK_TYPE, type);
        resultIt.putParcelableArrayListExtra(Extras.EXTRA_IMAGE_CHANGE_LIST, changePhoto);
        setResult(RESULT_OK, resultIt);
        finish();
    }
}
