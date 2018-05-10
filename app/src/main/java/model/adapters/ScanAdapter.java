package model.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import io.github.senerh.shonentouch.R;
import model.database.ShonenTouchContract;
import model.entities.Scan;

public class ScanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    private final OnItemClickListener mOnItemClickListener;
    private final OnItemLongClickListener mOnItemLongClickListener;
    private final SimpleCursorAdapter mCursorAdapter;

    public ScanAdapter(Context context, OnItemClickListener itemClickListener, OnItemLongClickListener itemLongClickListener) {
        super();
        mContext = context;
        mOnItemClickListener = itemClickListener;
        mOnItemLongClickListener = itemLongClickListener;
        mCursorAdapter = new SimpleCursorAdapter(context, R.layout.scan_recycler_view_item, null, new String[] { }, new int[] {  }, 0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ScanViewHolder(mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent), mOnItemClickListener, mOnItemLongClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Cursor c = mCursorAdapter.getCursor();
        if (c != null) {
            c.moveToPosition(position);
            ((ScanViewHolder) holder).mScanNameTextView.setText(c.getString(c.getColumnIndex(ShonenTouchContract.ScanColumns.NAME)));
            ((ScanViewHolder) holder).mDownloadStatusTextView.setText(c.getString(c.getColumnIndex(ShonenTouchContract.ScanColumns.DOWNLOAD_STATUS)));
            switch (Scan.Status.valueOf(c.getString(c.getColumnIndex(ShonenTouchContract.ScanColumns.STATUS)))) {
                case NOT_DOWNLOADED:
                    ((ScanViewHolder) holder).mScanCardView.setBackgroundColor(Color.parseColor("#d0cbcb"));
                    ((ScanViewHolder) holder).mDownloadStatusImageView.setImageResource(R.drawable.ic_file_download_black_24dp);
                    return;
                case DOWNLOAD_IN_PROGRESS:
                    ((ScanViewHolder) holder).mScanCardView.setBackgroundColor(Color.parseColor("#ff6600"));
                    ((ScanViewHolder) holder).mDownloadStatusImageView.setImageResource(R.drawable.ic_file_download_black_24dp);
                    return;
                case DOWNLOAD_COMPLETE:
                    ((ScanViewHolder) holder).mScanCardView.setBackgroundColor(Color.parseColor("#85ee0b"));
                    ((ScanViewHolder) holder).mDownloadStatusImageView.setImageResource(R.drawable.ic_check_circle_black_24dp);
                    return;
                case DOWNLOAD_STOPPED:
                    ((ScanViewHolder) holder).mScanCardView.setBackgroundColor(Color.parseColor("#fc0352"));
                    ((ScanViewHolder) holder).mDownloadStatusImageView.setImageResource(R.drawable.ic_cancel_black_24dp);
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
