package fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.shonen.shonentouch.R;

import java.util.ArrayList;
import java.util.List;

import activity.MainActivity;
import adapter.HomeAdapter;
import dao.preferences.FavoritesPreferencesDAO;
import dto.Manga;


public class HomeFragment extends ListFragment {

    private List<Manga> mangaList;
    private HomeAdapter homeAdapter;
    private ListView manga_list_view;
    private MainActivity mainActivity;
    private FavoritesPreferencesDAO favoritesPreferencesDAO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        favoritesPreferencesDAO = new FavoritesPreferencesDAO(getActivity());
        mangaList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(R.string.menu_item_accueil);

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        manga_list_view = (ListView) view.findViewById(android.R.id.list);
        homeAdapter = new HomeAdapter(getActivity().getBaseContext(), mangaList);
        manga_list_view.setAdapter(homeAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mangaList.clear();
        mangaList.addAll(favoritesPreferencesDAO.getFavoriteMangaList());
        homeAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    /**
     * Pour les sdk < 23
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ScanFragment scanFragment = ScanFragment.newInstance(mangaList.get(position));
        mainActivity.switchFragment(scanFragment);
    }
}
