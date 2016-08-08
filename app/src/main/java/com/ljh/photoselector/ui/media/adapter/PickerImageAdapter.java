package com.ljh.photoselector.ui.media.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ljh.photoselector.R;
import com.ljh.photoselector.model.PhotoModel;
import com.ljh.photoselector.util.ImageLoader;
import com.ljh.photoselector.util.LogUtils;
import com.ljh.photoselector.util.media.MediaImageThumbnailsCache;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuchao on 15-7-14.
 */
public class PickerImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = LogUtils.makeLogTag(PickerImageAdapter.class.getSimpleName());
    private List<PhotoModel> photos;

    public int selectedCount;
    public int maxSelectedCount = 9;
    private Context mContext;
    private OnPhotoClickListener onPhotoClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private OnPhotoSelectedListener onPhotoSelectedListener;


    public void setOnPhotoSelectedListener(OnPhotoSelectedListener listener) {
        this.onPhotoSelectedListener = listener;
    }

    public void setOnPhotoClickListener(OnPhotoClickListener listener) {
        this.onPhotoClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    public PickerImageAdapter(Context context) {
        photos = new ArrayList<>();
        mContext = context;
    }

    public List<PhotoModel> getPhotos() {
        return photos;
    }

    public void setData(List<PhotoModel> data, int selectedCount, int maxSelectedCount) {
        if (null != data) {
            this.selectedCount = selectedCount;
            this.maxSelectedCount = maxSelectedCount;
            photos = data;
            notifyDataSetChanged();
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        View imageView = View.inflate(parent.getContext(), R.layout.item_selector_image, null);
        holder = new ImageHolder(imageView);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ImageHolder imageHolder = (ImageHolder) holder;
        imageHolder.photo = photos.get(position);
        String thumbnailPath = MediaImageThumbnailsCache.getThumbnailPathByImageId(imageHolder.photo.id, imageHolder.photo.origin);
//        LogUtils.LOGD(TAG, thumbnailPath + "   " + imageHolder.photo.origin);
        ImageLoader.loadFromLocal(mContext, thumbnailPath, imageHolder.imageView);
        imageHolder.ivSelected.setImageResource(imageHolder.photo.isSelected ? R.mipmap.picker_image_selected : R.mipmap.picker_image_normal);


    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class ImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public PhotoModel photo;
        ImageView imageView;
        ImageView ivSelected;

        public ImageHolder(final View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            ivSelected = (ImageView) itemView.findViewById(R.id.iv_selected);
            itemView.setOnClickListener(this);
            ivSelected.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_selected: {
                    photo.isSelected = !photo.isSelected;
                    if (photo.isSelected) {
                        if (selectedCount >= maxSelectedCount) {
                            photo.isSelected = false;
                            if (null != onPhotoSelectedListener) {
                                onPhotoSelectedListener.selectedPhotoUpLimit(maxSelectedCount);
                            }
                        } else {
                            selectedCount++;
                            notifyItemChanged(getAdapterPosition());
                            if (null != onPhotoSelectedListener) {
                                onPhotoSelectedListener.addSelectedPhoto(photo);
                            }
                        }

                    } else {
                        selectedCount--;
                        notifyItemChanged(getAdapterPosition());
                        if (null != onPhotoSelectedListener) {
                            onPhotoSelectedListener.removeSelectedPhoto(photo);
                        }
                    }

                    break;
                }
                case R.id.ll_root:
                    if (null != onPhotoClickListener) {
                        onPhotoClickListener.onPhotoClick(photo, getAdapterPosition());
                    }
                    break;
            }
        }
    }

    public interface OnPhotoClickListener {
        void onPhotoClick(PhotoModel photo, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(PhotoModel photo);
    }

    public interface OnPhotoSelectedListener {
        void addSelectedPhoto(PhotoModel photo);

        void removeSelectedPhoto(PhotoModel photo);

        void selectedPhotoUpLimit(int limit);
    }
}
