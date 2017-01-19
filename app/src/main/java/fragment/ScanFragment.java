package fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.shonen.shonentouch.R;

import java.util.ArrayList;
import java.util.List;

import adapter.ScansAdapter;
import dao.shonentouch.InterfaceTaskShonentouchDAO;
import dao.shonentouch.ScanShonentouchDAO;
import dto.Manga;
import dto.Scan;

public class ScanFragment extends ListFragment implements InterfaceTaskShonentouchDAO<List<Scan>> {

    private static final String ID_SCAN_LIST = "fragment.ScanFragment.scanList";
    private NavigationView.OnNavigationItemSelectedListener mListener;
    private List<Scan> scanList;
    private ListView scan_list_view;
    private ScansAdapter scansAdapter;
    private Manga manga;
    private Dialog progressDialog;


    public ScanFragment() {
    }


    public static ScanFragment newInstance(Manga manga) {
        ScanFragment fragment = new ScanFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("manga", manga);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            scanList = new ArrayList<>();
            new ScanShonentouchDAO(this, (Manga)this.getArguments().getParcelable("manga")).execute();

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

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NavigationView.OnNavigationItemSelectedListener) {
            mListener = (NavigationView.OnNavigationItemSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void displayOnPostExecute(List<Scan> scanList) {
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
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Chargement");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<Scan> scanArrayList = new ArrayList<>(scanList);
        outState.putParcelableArrayList(ID_SCAN_LIST, scanArrayList);
    }
}
