package adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.shonen.shonentouch.R;

import java.util.List;

import dto.History;

/**
 * Created by Franck on 31/01/2017.
 */

public class HistoryAdapter extends ArrayAdapter<History> {
    private Context context;
    private List<History> historyList;

    public HistoryAdapter(Context context, List<History> historyList) {
        super(context, R.layout.element_history_list, historyList);
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        History history = getItem(position);

        convertView = inflater.inflate(R.layout.element_history_list, parent, false);
        TextView textView = (TextView) convertView.findViewById(R.id.manga_history);

        textView.setText(history.getManga().getName()+"-"+history.getScan().getNum());

        return convertView;
    }

    public History getItem(int position){
        return historyList.get(position);
    }
}
