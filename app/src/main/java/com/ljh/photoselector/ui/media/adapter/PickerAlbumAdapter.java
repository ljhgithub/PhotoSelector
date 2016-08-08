package com.ljh.photoselector.ui.media.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ljh.photoselector.R;
import com.ljh.photoselector.util.ImageLoader;
import com.ljh.photoselector.model.PhotoFolder;
import com.ljh.photoselector.util.LogUtils;

import java.util.ArrayList;

/**
 * Created by ljh on 2016/3/3.
 */
public class PickerAlbumAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = LogUtils.makeLogTag(PickerAlbumAdapter.class.getSimpleName());
    private ArrayList<PhotoFolder> folders;
    private Context ctx;
    private OnAlbumClickListener onAlbumClickListener;

    public void setOnAlbumClickListener(OnAlbumClickListener listener){
        onAlbumClickListener =listener;
    }

    public PickerAlbumAdapter(Context context, ArrayList<PhotoFolder> datas) {
        ctx = context;
        if (null == datas) {
            folders = new ArrayList<>();
        }
        folders = datas;
    }




    @Override
    public AlbumHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_album, parent, false);
        return new AlbumHolder(view);
    }

    @Override
    public void onBindViewHolder( RecyclerView.ViewHolder holder, final int position) {

        AlbumHolder albumHolder= (AlbumHolder) holder;
        albumHolder.folder=folders.get(position);
        ImageLoader.loadFromLocal(ctx,  albumHolder.folder.firstImagePath, albumHolder.ivAlbum);
       albumHolder.tvAlbumDescription.setText(albumHolder.folder.name + "\n" + albumHolder.folder.getCount() + "张");

    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    public class AlbumHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public  ImageView ivAlbum;
        public TextView tvAlbumDescription;
        public PhotoFolder folder;
        public AlbumHolder(View view) {
            super(view);
            ivAlbum = (ImageView) view.findViewById(R.id.iv_album);
            tvAlbumDescription= (TextView) view.findViewById(R.id.tv_album_description);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null != onAlbumClickListener){
                onAlbumClickListener.onAlbumClick(folder);
            }
        }
    }

    public interface OnAlbumClickListener {
        void onAlbumClick(PhotoFolder  folder);
    }
}
