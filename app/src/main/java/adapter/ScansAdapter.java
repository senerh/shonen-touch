package adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.shonen.shonentouch.R;

import java.util.List;

import dto.Scan;


public class ScansAdapter extends ArrayAdapter<Scan> {

    private List<Scan> scanList;
    private Context context;

    public ScansAdapter(Context context, List<Scan> scanList) {
        super(context, R.layout.element_scan_list, scanList);
        this.scanList = scanList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.element_scan_list, parent, false);
        TextView name = (TextView) rowView.findViewById(R.id.name_scan);

        String text = context.getString(R.string.scan, scanList.get(position).getNum());
        name.setText(text);
        return rowView;
    }

    public Scan getItem(int position){
        return scanList.get(position);
    }
}
