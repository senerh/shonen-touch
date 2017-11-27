package model.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import model.database.ShonenTouchContract;

public class MangaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    private final OnItemClickListener mOnItemClickListener;
    private final SimpleCursorAdapter mCursorAdapter;

    public MangaAdapter(Context context, OnItemClickListener itemClickListener) {
        super();
        mContext = context;
        mOnItemClickListener = itemClickListener;
        mCursorAdapter = new SimpleCursorAdapter(context, android.R.layout.simple_list_item_1, null, new String[] { }, new int[] {  }, 0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.manga_recycler_view_item, parent, false);
//        return new MangaViewHolder(itemView, mOnItemClickListener);
        return new MangaViewHolder(mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent), mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Cursor c = mCursorAdapter.getCursor();

        if (c != null) {
            c.moveToPosition(position);
            ((MangaViewHolder) holder).mMangaNameTextView.setText(c.getString(c.getColumnIndex(ShonenTouchContract.MangaColumns.NAME)));
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
