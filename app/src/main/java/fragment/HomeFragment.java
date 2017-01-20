package fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.shonen.shonentouch.R;

import java.util.List;

import adapter.HomeAdapter;
import dao.preferences.FavoritesPreferencesDAO;
import dto.Manga;


public class HomeFragment extends ListFragment {

    private List<Manga> mangaList;
    private HomeAdapter homeAdapter;
    private ListView manga_list_view;

    public HomeFragment() {
    }

    public static HomeFragment newInstance(Context context) {
        HomeFragment fragment = new HomeFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FavoritesPreferencesDAO favoritesPreferencesDAO = new FavoritesPreferencesDAO(getActivity());
        mangaList = favoritesPreferencesDAO.getFavoriteMangaList();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        manga_list_view = (ListView) view.findViewById(android.R.id.list);
        homeAdapter = new HomeAdapter(getActivity().getBaseContext(), mangaList);
        manga_list_view.setAdapter(homeAdapter);

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
    public void onListItemClick(ListView l, View v, int position, long id) {
        FragmentTransaction tx = getActivity().getSupportFragmentManager().beginTransaction();
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        ScanFragment scanFragment = ScanFragment.newInstance(mangaList.get(position));
        tx.replace(R.id.main_content, scanFragment, "activeFragment");
        tx.commit();

        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
}
