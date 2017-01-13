package activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.shonen.shonentouch.R;

import java.util.ArrayList;
import java.util.List;

import adapter.FavoritesAdapter;
import dao.preferences.PreferencesDAO;
import dao.shonentouch.InterfaceTaskShonentouchDAO;
import dao.shonentouch.MangaShonentouchDAO;
import dto.Manga;

public class FavoritesActivity extends AppCompatActivity implements InterfaceTaskShonentouchDAO<List<Manga>> {

    private static final String ID_MANGA_LIST = "activity.FavoritesActivity.mangaList";
    private static final String KEY_MANGAS_FAVORIS = "favoris";

    private ListView mangaListView;
    private List<Manga> mangaList;
    private String favoriteMangaListJson;
    private List<Manga> favoriteMangaList;
    private FavoritesAdapter favoritesAdapter;

    private ProgressDialog progressDialog;
    private PreferencesDAO preferencesDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites_layout);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferencesDAO = new PreferencesDAO(getBaseContext());
        favoriteMangaListJson = preferencesDAO.readPreferences(KEY_MANGAS_FAVORIS);

        mangaListView = (ListView) findViewById(R.id.manga_list);

        Button validate = (Button) findViewById(R.id.validate);

        if (savedInstanceState == null) {
            mangaList = new ArrayList<>();
            favoritesAdapter = new FavoritesAdapter(getBaseContext(), mangaList);
            mangaListView.setAdapter(favoritesAdapter);
            new MangaShonentouchDAO(this).execute();
        } else {
            mangaList = savedInstanceState.getParcelableArrayList(ID_MANGA_LIST);
            favoritesAdapter = new FavoritesAdapter(getBaseContext(), mangaList);
            mangaListView.setAdapter(favoritesAdapter);
        }

        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Manga> newFavoritesList = new ArrayList<>();
                for (int i = 0; i < favoritesAdapter.getCount(); i++) {
                    Manga manga = favoritesAdapter.getItem(i);
                    if (manga.isChecked()) {
                        newFavoritesList.add(manga);
                    }
                }
                Toast.makeText(getBaseContext(), "Favoris sauvegardés", Toast.LENGTH_SHORT).show();
                String newFavoritesListJson = preferencesDAO.mangaToJson(newFavoritesList);
                preferencesDAO.savePreferences(KEY_MANGAS_FAVORIS, newFavoritesListJson);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        ArrayList<Manga> mangaArrayList = new ArrayList<>(mangaList);
        savedInstanceState.putParcelableArrayList(ID_MANGA_LIST, mangaArrayList);
    }

    @Override
    public void displayOnPostExecute(List<Manga> mangaList) {
        progressDialog.dismiss();
        if (mangaList == null) {
            Toast.makeText(getApplicationContext(), "Aucun manga n'a été trouvé, vérifiez votre connexion internet.", Toast.LENGTH_LONG).show();
        } else {
            this.mangaList.addAll(mangaList);
            if (!favoriteMangaListJson.equals("No preferencesDAO")) {
                favoriteMangaList = preferencesDAO.jsonToManga(favoriteMangaListJson);
                for (Manga manga:mangaList) {
                    if (favoriteMangaList.contains(manga)){
                        manga.setChecked(true);
                    }
                }
            }
            favoritesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void displayOnPreExecute() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Chargement");
        progressDialog.setMessage("Veuillez patienter pendant le chargement des mangas.");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
