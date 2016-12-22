package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.shonen.shonentouch.R;

import java.util.List;

import dto.Manga;

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
        View rowView = inflater.inflate(R.layout.favorites_list_layout, parent, false);
        TextView name = (TextView) rowView.findViewById(R.id.name);
        name.setText(mangaList.get(position).getName());
        return rowView;
    }
}
