package adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import dto.Manga;
import io.github.senerh.shonentouch.R;


public class HomeAdapter extends ArrayAdapter<Manga> {

    private List<Manga> mangaList;

    public HomeAdapter(Context context, List<Manga> mangaList) {
        super(context, R.layout.element_home_list, mangaList);
        this.mangaList = mangaList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.element_home_list, parent, false);
        TextView name = (TextView) rowView.findViewById(R.id.nameHome);
        name.setText(mangaList.get(position).getName());

        return rowView;
    }

    public Manga getItem(int position){
        return mangaList.get(position);
    }
}
