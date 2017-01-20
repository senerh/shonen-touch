package adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.shonen.shonentouch.R;

import java.util.List;

import dto.Manga;
import holder.MangaViewHolder;

public class FavoritesAdapter extends ArrayAdapter<Manga> {

    private Context context;

    public FavoritesAdapter(Context context, List<Manga> mangaList) {
        super(context, R.layout.element_favorite_list, mangaList);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Manga manga = getItem(position);
        CheckBox checkBox;
        TextView textView;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.element_favorite_list, parent, false);
            textView = (TextView) convertView.findViewById(R.id.name_favoris);
            checkBox = (CheckBox) convertView.findViewById(R.id.checkbox_favoris);
            checkBox.setChecked(manga.isChecked());
            convertView.setTag(new MangaViewHolder(textView, checkBox));
        } else {
            MangaViewHolder viewHolder = (MangaViewHolder) convertView.getTag();
            checkBox = viewHolder.getCheckBox();
            textView = viewHolder.getTextView();
        }

        checkBox.setTag(manga);
        checkBox.setChecked(manga.isChecked());
        textView.setText(manga.getName());

        return convertView;
    }

}
