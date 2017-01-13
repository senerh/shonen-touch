package adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.shonen.shonentouch.R;

import java.util.List;

import dao.preferences.PreferencesDAO;
import dto.Manga;
import holder.MangaViewHolder;

public class FavoritesAdapter extends ArrayAdapter<Manga> {


    private Context context;
    private List<Manga> mangaList;


    public FavoritesAdapter(Context context, List<Manga> mangaList) {
        super(context, R.layout.favorites_list_layout, mangaList);
        this.context = context;
        this.mangaList = mangaList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Manga manga = (Manga) this.getItem(position);
        CheckBox checkBox;
        TextView textView;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.favorites_list_layout, null);
            textView = (TextView) convertView.findViewById(R.id.name_favoris);
            checkBox = (CheckBox) convertView.findViewById(R.id.checkbox_favoris);
            checkBox.setChecked(manga.isChecked());
            convertView.setTag(new MangaViewHolder(textView, checkBox));

            checkBox.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    CheckBox cb = (CheckBox) v;
                    Manga manga = (Manga) cb.getTag();
                    manga.setChecked(cb.isChecked());
                }
            });
        }else{
            MangaViewHolder viewHolder = (MangaViewHolder) convertView
                    .getTag();
            checkBox = viewHolder.getCheckBox();
            textView = viewHolder.getTextView();
        }

        checkBox.setTag(manga);
        checkBox.setChecked(manga.isChecked());
        textView.setText(manga.getName());


        /*View rowView = inflater.inflate(R.layout.favorites_list_layout, parent, false);
        checkBox = (CheckBox) rowView.findViewById(R.id.checkbox_favoris);
        TextView name = (TextView) rowView.findViewById(R.id.name_favoris);
        name.setText(mangaList.get(position).getName());
        return rowView;
        */

        return convertView;
    }

}
