package model.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import io.github.senerh.shonentouch.R;


public class MangaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    // Views
    public TextView mMangaNameTextView;
    public ImageView mMangaIconImageView;

    // Listener
    private OnItemClickListener mAdapterListener;

    public MangaViewHolder(View itemView) {
        super(itemView);
        mMangaNameTextView = (TextView) itemView.findViewById(R.id.text_view_manga_name);
        mMangaIconImageView = (ImageView) itemView.findViewById(R.id.image_view_manga_icon);
    }

    public MangaViewHolder(View itemView, OnItemClickListener listener) {
        this(itemView);
        mAdapterListener = listener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (mAdapterListener != null) {
            switch (view.getId()) {
                default:
                    mAdapterListener.onItemClick(view, getAdapterPosition());
                    break;
            }
        }
    }
}
