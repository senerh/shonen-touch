package adapter;

import android.content.Context;
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
        super(context, R.layout.favorites_list_layout, mangaList);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Manga manga = getItem(position);
        CheckBox checkBox;
        TextView textView;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.favorites_list_layout, null);
            textView = (TextView) convertView.findViewById(R.id.name_favoris);
            checkBox = (CheckBox) convertView.findViewById(R.id.checkbox_favoris);
            checkBox.setChecked(manga.isChecked());
            convertView.setTag(new MangaViewHolder(textView, checkBox));

            checkBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    Manga manga = (Manga) cb.getTag();
                    manga.setChecked(cb.isChecked());
                }
            });
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
