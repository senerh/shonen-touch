package fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shonen.shonentouch.R;

import java.util.ArrayList;
import java.util.List;

import activity.PageActivity;
import adapter.HistoryAdapter;
import dao.preferences.HistoryPreferencesDAO;
import dao.shonentouch.InterfaceTaskShonentouchService;
import dto.History;
import dto.Manga;
import holder.MangaViewHolder;
import listener.ChatButtonListener;
import listener.HistoryCleanerListener;

/**
 * Created by Franck on 30/01/2017.
 */

public class HistoryFragment extends ListFragment {
    private static final String ID_HISTORY_LIST = "activity.HistoryFragment.historyList";
    private ListView history_list_view;
    private List<History> historyList;
    private HistoryAdapter historyAdapter;
    private HistoryPreferencesDAO historyPreferencesDAO;



    private TextView emptyTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        historyPreferencesDAO = new HistoryPreferencesDAO(getActivity());

        if (savedInstanceState == null) {
            historyList = historyPreferencesDAO.getHistoryList();
        } else {
            historyList = savedInstanceState.getParcelableArrayList(ID_HISTORY_LIST);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(R.string.menu_item_history);

        View view = inflater.inflate(R.layout.fragment_history, container, false);

        history_list_view = (ListView) view.findViewById(android.R.id.list);
        emptyTextView = (TextView) view.findViewById(R.id.empty_history);

        historyAdapter = new HistoryAdapter(getActivity().getBaseContext(), historyList);
        history_list_view.setAdapter(historyAdapter);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(
                new HistoryCleanerListener(this)
        );

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        historyList.clear();
        historyList.addAll(historyPreferencesDAO.getHistoryList());
        if (historyList.isEmpty()){
            emptyTextView.setText("Historique vide");
        }else{
            emptyTextView.setText("");
        }
        historyAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<History> historyArrayList = new ArrayList<>(historyList);
        outState.putParcelableArrayList(ID_HISTORY_LIST, historyArrayList);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        History current_history = historyList.get(position);
        Toast.makeText(getActivity().getApplicationContext(), current_history.getManga().getName()+" - "+current_history.getScan().getNum(), Toast.LENGTH_LONG).show();

        Intent myIntent = new Intent(getActivity(), PageActivity.class);

        Bundle b = new Bundle();
        b.putParcelable(ScanFragment.ID_MANGA_PARCELABLE, current_history.getManga());
        b.putParcelable(ScanFragment.ID_SCAN_PARCELABLE, current_history.getScan());
        myIntent.putExtras(b);

        startActivity(myIntent);

    }

    public List<History> getHistoryList() {
        return historyList;
    }

    public HistoryAdapter getHistoryAdapter() {
        return historyAdapter;
    }

    public TextView getEmptyTextView() {
        return emptyTextView;
    }
}
