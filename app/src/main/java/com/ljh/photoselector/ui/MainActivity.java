package com.ljh.photoselector.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ljh.photoselector.R;
import com.ljh.photoselector.constant.Extras;
import com.ljh.photoselector.model.PhotoModel;
import com.ljh.photoselector.ui.media.activity.PickerImageActivity;
import com.ljh.photoselector.util.LogUtils;
import com.ljh.photoselector.util.StorageType;
import com.ljh.photoselector.util.StorageUtil;
import com.ljh.photoselector.util.StringUtil;
import com.ljh.photoselector.util.media.ImageUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_CODE_CAMERA = 10;
    public final static String TAG = LogUtils.makeLogTag(MainActivity.class.getSimpleName());
    private Button btnLocalImage, btnCamera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLocalImage = (Button) findViewById(R.id.btn_local_image);
        btnCamera = (Button) findViewById(R.id.btn_camera);
        btnLocalImage.setOnClickListener(this);
        btnCamera.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case PickerImageActivity.REQUEST_IMAGE_FROM_LOCAL:
                    ArrayList<PhotoModel> selectedPhotos = data.getParcelableArrayListExtra(Extras.EXTRA_IMAGE_LISTS);
                    LogUtils.LOGD(TAG, "selectedPhotos " + selectedPhotos.size());
                    break;
                case REQUEST_CODE_CAMERA:
                    LogUtils.LOGD(TAG, "onActivityResult ");
                    onPickedCamera(data, REQUEST_CODE_CAMERA);
                    break;
                default:
                    break;
            }
        }
    }

    public static final String JPG = ".jpg";
    public static final String MIME_JPEG = "image/jpeg";
    String outPath;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_local_image:
                PickerImageActivity.start(this);
                break;
            case R.id.btn_camera:
                String filename = StringUtil.get32UUID() + JPG;
                StorageUtil.init(this, null);
                outPath = StorageUtil.getWritePath(filename, StorageType.TYPE_TEMP);
                LogUtils.LOGD(TAG, "path" + outPath);
                pickFromCamera(outPath);
                break;
            default:
                break;
        }
    }


    private void pickFromCamera(String outPath) {
//        try {
        if (TextUtils.isEmpty(outPath)) {
            Toast.makeText(this, "存储空间不足，无法保存此图片", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        File outputFile = new File(outPath);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputFile));
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
//        } catch (ActivityNotFoundException e) {
//            LogUtils.LOGD(TAG,e.getMessage());
//            finish();
//        } catch (Exception e) {
//            Toast.makeText(this, "SD卡被拔出或存储空间不足，无法保存照片", Toast.LENGTH_LONG).show();
//            LogUtils.LOGD(TAG,e.getMessage());
//            finish();
//        }

    }

    private void onPickedCamera(Intent data, int code) {
        try {
            String photoPath = pathFromResult(data);
            if (!TextUtils.isEmpty(photoPath)) {
//                    Intent result = new Intent();
//                    result.putExtra(Extras.EXTRA_FILE_PATH, photoPath);
//                    setResult(RESULT_OK, result);
                LogUtils.LOGD(TAG, "original photo path " + photoPath);
                handleImage(photoPath);
            }
//                finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "获取图片出错", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private boolean handleImage(String photoPath) {
        if (TextUtils.isEmpty(photoPath)) {
            Toast.makeText(this, "获取图片出错", Toast.LENGTH_LONG).show();
            return false;
        }
        File imageFile = new File(photoPath);
        LogUtils.LOGD(TAG, "original size " + imageFile.length());
        File scaledImageFile = ImageUtil.getScaledImageFile(imageFile, MIME_JPEG);
        LogUtils.LOGD(TAG, "scale photo path " + scaledImageFile);
//        AttachmentStore.delete(photoPath);
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(getContentResolver(),
                    imageFile.getAbsolutePath(), imageFile.getName(), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + imageFile.getPath())));
        String thumbPhotoPath = ImageUtil.makeThumbnail(this, scaledImageFile);
        LogUtils.LOGD(TAG, "thumbnail path " + thumbPhotoPath);
        return true;
    }

    private String pathFromResult(Intent data) {
        if (data == null || data.getData() == null) {
            return outPath;
        }

        Uri uri = data.getData();
        Cursor cursor = getContentResolver()
                .query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
        if (cursor == null) {
            // miui 2.3 有可能为null
            return uri.getPath();
        } else {
            if (uri.toString().contains("content://com.android.providers.media.documents/document/image")) { // htc 某些手机
                // 获取图片地址
                String _id = null;
                String uridecode = uri.decode(uri.toString());
                int id_index = uridecode.lastIndexOf(":");
                _id = uridecode.substring(id_index + 1);
                Cursor mcursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, " _id = " + _id,
                        null, null);
                mcursor.moveToFirst();
                int column_index = mcursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                outPath = mcursor.getString(column_index);
                if (!mcursor.isClosed()) {
                    mcursor.close();
                }
                if (!cursor.isClosed()) {
                    cursor.close();
                }
                return outPath;

            } else {
                cursor.moveToFirst();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                outPath = cursor.getString(column_index);
                if (!cursor.isClosed()) {
                    cursor.close();
                }
                return outPath;
            }
        }
    }

}
