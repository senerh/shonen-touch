package model.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


public class ScanViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    // Views
    public TextView mScanNameTextView;

    // Listener
    private OnItemClickListener mAdapterListener;

    public ScanViewHolder(View itemView) {
        super(itemView);
        mScanNameTextView = (TextView) itemView.findViewById(android.R.id.text1);
    }

    public ScanViewHolder(View itemView, OnItemClickListener listener) {
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
