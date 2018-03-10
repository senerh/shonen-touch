package model.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import io.github.senerh.shonentouch.R;


public class ScanViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    // Views
    public TextView mScanNameTextView;
    public TextView mDownloadStatusTextView;
    public ImageView mDownloadStatusImageView;
    public CardView mScanCardView;

    // Listener
    private OnItemClickListener mAdapterListener;
    private OnItemLongClickListener mAdapterLongClickListener;

    public ScanViewHolder(View itemView) {
        super(itemView);
        mScanNameTextView = (TextView) itemView.findViewById(R.id.text_view_scan_name);
        mDownloadStatusTextView = (TextView) itemView.findViewById(R.id.text_view_download_status);
        mDownloadStatusImageView = (ImageView) itemView.findViewById(R.id.image_view_download_status);
        mScanCardView = (CardView) itemView.findViewById(R.id.card_view_scan);
    }

    public ScanViewHolder(View itemView, OnItemClickListener listener, OnItemLongClickListener longClickListener) {
        this(itemView);
        mAdapterListener = listener;
        mAdapterLongClickListener = longClickListener;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
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

    @Override
    public boolean onLongClick(View view) {
        if (mAdapterLongClickListener != null) {
            switch (view.getId()) {
                default:
                    mAdapterLongClickListener.onItemLongClick(view, getAdapterPosition());
                    break;
            }
        }

        return true;
    }
}
