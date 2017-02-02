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
import android.widget.Toast;

import com.shonen.shonentouch.R;

import java.util.ArrayList;
import java.util.List;

import activity.PageActivity;
import adapter.ScansAdapter;
import dao.shonentouch.InterfaceTaskShonentouchService;
import dao.shonentouch.ScanShonentouchService;
import dto.Manga;
import dto.Scan;
import listener.ChatButtonListener;

public class ScanFragment extends ListFragment implements InterfaceTaskShonentouchService<List<Scan>> {

    public static final String ID_SCAN_LIST = "fragment.ScanFragment.ID_SCAN_LIST";
    public static final String ID_MANGA_PARCELABLE = "fragment.ScanFragment.ID_MANGA_PARCELABLE";
    public static final String ID_SCAN_PARCELABLE = "fragment.ScanFragment.ID_SCAN_PARCELABLE";

    private List<Scan> scanList;
    private ListView scan_list_view;
    private ScansAdapter scansAdapter;
    private ProgressDialog progressDialog;

    public ScanFragment() {
    }

    public static ScanFragment newInstance(Manga manga) {
        ScanFragment fragment = new ScanFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(ID_MANGA_PARCELABLE, manga);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            scanList = new ArrayList<>();
            new ScanShonentouchService(this,
                    (Manga)this.getArguments().getParcelable(ID_MANGA_PARCELABLE)).execute();
        } else {
            scanList = savedInstanceState.getParcelableArrayList(ID_SCAN_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_manga, container, false);
        scan_list_view = (ListView) view.findViewById(android.R.id.list);

        scansAdapter = new ScansAdapter(getActivity().getBaseContext(), scanList);
        scan_list_view.setAdapter(scansAdapter);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(
                new ChatButtonListener(
                        (Manga)this.getArguments().getParcelable(ID_MANGA_PARCELABLE),
                        getActivity()
                )
        );

        Manga manga = this.getArguments().getParcelable(ID_MANGA_PARCELABLE);

        getActivity().setTitle(manga.getName());

        return view;
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
    public void displayOnPostExecute(List<Scan> scanList) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        progressDialog.dismiss();
        if (scanList == null) {
            Toast.makeText(getActivity().getApplicationContext(), "Aucun scan n'a été trouvé, vérifiez votre connexion internet.", Toast.LENGTH_LONG).show();

        } else {
            this.scanList.addAll(scanList);
            scansAdapter.notifyDataSetChanged();
        }
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Scan> scanArrayList = new ArrayList<>(scanList);
        outState.putParcelableArrayList(ID_SCAN_LIST, scanArrayList);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent myIntent = new Intent(getActivity(), PageActivity.class);

        Bundle b = new Bundle();
        b.putParcelable(ID_MANGA_PARCELABLE, this.getArguments().getParcelable(ID_MANGA_PARCELABLE));
        b.putParcelable(ID_SCAN_PARCELABLE, scanList.get(position));
        b.putParcelableArrayList(ID_SCAN_LIST, new ArrayList<>(scanList));
        myIntent.putExtras(b);

        startActivity(myIntent);
    }
}
