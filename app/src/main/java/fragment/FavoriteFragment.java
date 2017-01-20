package fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;
import com.shonen.shonentouch.R;
import java.util.ArrayList;
import java.util.List;
import adapter.FavoritesAdapter;
import dao.preferences.FavoritesPreferencesDAO;
import dao.shonentouch.InterfaceTaskShonentouchService;
import dao.shonentouch.MangaShonentouchService;
import dto.Manga;
import holder.MangaViewHolder;


public class FavoriteFragment extends ListFragment implements InterfaceTaskShonentouchService<List<Manga>> {

    private static final String ID_MANGA_LIST = "activity.FavoritesFragment.mangaList";
    private ListView manga_list_view;
    private List<Manga> mangaList;
    private FavoritesAdapter favoritesAdapter;
    private ProgressDialog progressDialog;
    private FavoritesPreferencesDAO favoritesPreferencesDAO;

    public FavoriteFragment() {

    }


    public static FavoriteFragment newInstance(Context context) {
        FavoriteFragment fragment = new FavoriteFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        favoritesPreferencesDAO = new FavoritesPreferencesDAO(getActivity());

        if (savedInstanceState == null) {
            mangaList = new ArrayList<>();
            new MangaShonentouchService(this).execute();

        } else {
            mangaList = savedInstanceState.getParcelableArrayList(ID_MANGA_LIST);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        manga_list_view = (ListView) view.findViewById(android.R.id.list);

        favoritesAdapter = new FavoritesAdapter(getActivity().getBaseContext(), mangaList);
        manga_list_view.setAdapter(favoritesAdapter);
        
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
    public void displayOnPostExecute(List<Manga> mangaList) {
        progressDialog.dismiss();
        if (mangaList == null) {
            Toast.makeText(getActivity().getApplicationContext(), "Aucun manga n'a été trouvé, vérifiez votre connexion internet.", Toast.LENGTH_LONG).show();
        } else {
            this.mangaList.addAll(mangaList);
            List<Manga> favoriteMangaList = favoritesPreferencesDAO.getFavoriteMangaList();
            for (Manga manga : mangaList) {
                if (favoriteMangaList.contains(manga)){
                    manga.setChecked(true);
                }
            }
            favoritesAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void displayOnPreExecute() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Chargement");
        progressDialog.setMessage("Veuillez patienter pendant le chargement des mangas.");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<Manga> mangaArrayList = new ArrayList<>(mangaList);
        outState.putParcelableArrayList(ID_MANGA_LIST, mangaArrayList);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        MangaViewHolder viewHolder = (MangaViewHolder) v.getTag();
        CheckBox checkBox = viewHolder.getCheckBox();
        checkBox.toggle();

        Manga selectedManga = (Manga) checkBox.getTag();
        selectedManga.setChecked(checkBox.isChecked());

        List<Manga> newFavoriteMangaList = new ArrayList<>();
        for (int i = 0; i < favoritesAdapter.getCount(); i++) {
            Manga manga = favoritesAdapter.getItem(i);
            if (manga.isChecked()) {
                newFavoriteMangaList.add(manga);
            }
        }
        favoritesPreferencesDAO.saveFavoriteMangaList(newFavoriteMangaList);
    }
}
