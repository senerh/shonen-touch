package model.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.github.senerh.shonentouch.R;
import model.entities.Manga;

public class MangaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Manga> mMangaList;
    private final MangaAdapterListener mMangaAdapterListener;

    public MangaAdapter(List<Manga> mangaList, MangaAdapterListener itemClickListener) {
        super();
        mMangaList = mangaList;
        mMangaAdapterListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.manga_recycler_view_item, parent, false);
        return new MangaViewHolder(itemView, mMangaAdapterListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MangaViewHolder) holder).mMangaNameTextView.setText(mMangaList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mMangaList.size();
    }

}
