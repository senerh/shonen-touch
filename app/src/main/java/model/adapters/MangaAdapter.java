package model.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import io.github.senerh.shonentouch.R;
import model.database.ShonenTouchContract;

public class MangaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    private final OnItemClickListener mOnItemClickListener;
    private final SimpleCursorAdapter mCursorAdapter;

    public MangaAdapter(Context context, OnItemClickListener itemClickListener) {
        super();
        mContext = context;
        mOnItemClickListener = itemClickListener;
        mCursorAdapter = new SimpleCursorAdapter(context, R.layout.manga_recycler_view_item, null, new String[] { }, new int[] {  }, 0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MangaViewHolder(mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent), mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Cursor c = mCursorAdapter.getCursor();

        if (c != null) {
            c.moveToPosition(position);
            ((MangaViewHolder) holder).mMangaNameTextView.setText(c.getString(c.getColumnIndex(ShonenTouchContract.MangaColumns.NAME)));
            ((MangaViewHolder) holder).mLastScanTextView.setText(mContext.getString(R.string.lastScan, c.getString(c.getColumnIndex(ShonenTouchContract.MangaColumns.LAST_SCAN))));
            String filePath = c.getString(c.getColumnIndex(ShonenTouchContract.MangaColumns.ICON_PATH));
            if (!"".equals(filePath)) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                ((MangaViewHolder) holder).mMangaIconImageView.setImageBitmap(BitmapFactory.decodeFile(filePath, options));
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
