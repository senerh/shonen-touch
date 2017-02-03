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
import android.widget.ListView;
import android.widget.TextView;

import com.shonen.shonentouch.R;

import java.util.ArrayList;
import java.util.List;

import activity.PageActivity;
import adapter.HistoryAdapter;
import dao.preferences.HistoryPreferencesDAO;
import dao.shonentouch.InterfaceTaskShonentouchService;
import dao.shonentouch.ScanShonentouchService;
import dto.History;
import dto.Scan;
import listener.HistoryCleanerListener;

public class HistoryFragment extends ListFragment implements InterfaceTaskShonentouchService<List<Scan>> {

    private ListView history_list_view;
    private List<History> historyList;
    private HistoryAdapter historyAdapter;
    private HistoryPreferencesDAO historyPreferencesDAO;

    private TextView emptyTextView;
    private ProgressDialog progressDialog;
    private List<Scan> scanList;
    private History current_history;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        historyPreferencesDAO = new HistoryPreferencesDAO(getActivity());
        historyList = historyPreferencesDAO.getHistoryList();
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
        if (historyList.isEmpty()) {
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            emptyTextView.setVisibility(View.INVISIBLE);
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
    public void onListItemClick(ListView l, View v, int position, long id) {
        current_history = historyList.get(position);
        new ScanShonentouchService(this, current_history.getManga()).execute();
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

    @Override
    public void displayOnPostExecute(List<Scan> retrievedData) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        progressDialog.dismiss();
        scanList = new ArrayList<>();
        if (retrievedData != null) {
            scanList.addAll(retrievedData);
        }

        Intent myIntent = new Intent(getActivity(), PageActivity.class);

        Bundle b = new Bundle();
        b.putParcelable(ScanFragment.ID_MANGA_PARCELABLE, current_history.getManga());
        b.putParcelable(ScanFragment.ID_SCAN_PARCELABLE, current_history.getScan());
        b.putParcelableArrayList(ScanFragment.ID_SCAN_LIST, new ArrayList<>(scanList));
        myIntent.putExtras(b);

        startActivity(myIntent);
    }

    @Override
    public void displayOnPreExecute() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Chargement");
        progressDialog.setMessage("Veuillez patienter pendant le chargement des scans");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
}
