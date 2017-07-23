package model.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


public class MangaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    // Views
    public TextView mMangaNameTextView;

    // Listener
    private OnItemClickListener mAdapterListener;

    public MangaViewHolder(View itemView) {
        super(itemView);
        mMangaNameTextView = (TextView) itemView.findViewById(android.R.id.text1);
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
                    mAdapterListener.onMangaClick(view, getAdapterPosition());
                    break;
            }
        }
    }
}
