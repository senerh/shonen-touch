package model.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import model.database.ShonenTouchContract;
import model.entities.Scan;

public class ScanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    private final OnItemClickListener mOnItemClickListener;
    private final SimpleCursorAdapter mCursorAdapter;

    public ScanAdapter(Context context, OnItemClickListener itemClickListener) {
        super();
        mContext = context;
        mOnItemClickListener = itemClickListener;
        mCursorAdapter = new SimpleCursorAdapter(context, android.R.layout.simple_list_item_1, null, new String[] { }, new int[] {  }, 0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ScanViewHolder(mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent), mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        Cursor c = mCursorAdapter.getCursor();
//
//        if (c != null) {
//            c.moveToPosition(position);
//            ((ScanViewHolder) holder).mScanNameTextView.setText(c.getString(c.getColumnIndex(ShonenTouchContract.ScanColumns.NAME)));
//        }

        Cursor c = mCursorAdapter.getCursor();
        if (c != null) {
            c.moveToPosition(position);
            ((ScanViewHolder) holder).mScanNameTextView.setText(c.getString(c.getColumnIndex(ShonenTouchContract.ScanColumns.NAME)));
            switch (Scan.Status.valueOf(c.getString(c.getColumnIndex(ShonenTouchContract.ScanColumns.STATUS)))) {
                case NOT_DOWNLOADED:
                    ((ScanViewHolder) holder).mScanNameTextView.setBackgroundColor(Color.parseColor("#d0cbcb"));
                    return;
                case DOWNLOAD_IN_PROGRESS:
                    ((ScanViewHolder) holder).mScanNameTextView.setBackgroundColor(Color.parseColor("#ff6600"));
                    return;
                case DOWNLOAD_COMPLETE:
                    ((ScanViewHolder) holder).mScanNameTextView.setBackgroundColor(Color.parseColor("#85ee0b"));
                    return;
                default:
            }
        }
    }

    @Override
    public int getItemCount() {
        return mCursorAdapter.getCount();
    }

    public void swapCursor(Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
        notifyDataSetChanged();
    }
}
