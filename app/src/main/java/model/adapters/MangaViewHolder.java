package model.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import io.github.senerh.shonentouch.R;


public class MangaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    // Views
    public TextView mMangaNameTextView;

    // Listener
    private MangaAdapterListener mAdapterListener;

    public MangaViewHolder(View itemView) {
        super(itemView);
        mMangaNameTextView = (TextView) itemView.findViewById(R.id.text_view_manga_name);
    }

    public MangaViewHolder(View itemView, MangaAdapterListener listener) {
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
