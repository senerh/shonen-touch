package activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.shonen.shonentouch.R;

import java.util.ArrayList;
import java.util.List;

import adapter.FavoritesAdapter;
import dao.shonentouch.MangaShonentouchDAO;
import dto.Manga;

public class FavoritesActivity extends AppCompatActivity implements InterfaceTaskActivity<List<Manga>>{

    private static final String ID_MANGA_LIST = "activity.FavoritesActivity.mangaList";

    private ListView mangaListView;
    private List<Manga> mangaList;
    private FavoritesAdapter favoritesAdapter;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites_layout);

        mangaListView = (ListView) findViewById(R.id.manga_list);

        if (savedInstanceState == null) {
            mangaList = new ArrayList<Manga>();
            favoritesAdapter = new FavoritesAdapter(getBaseContext(), mangaList);
            mangaListView.setAdapter(favoritesAdapter);
            new MangaShonentouchDAO(this).execute();
        } else {
            mangaList = savedInstanceState.getParcelableArrayList(ID_MANGA_LIST);
            favoritesAdapter = new FavoritesAdapter(getBaseContext(), mangaList);
            mangaListView.setAdapter(favoritesAdapter);
        }
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
            favoritesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void displayOnPreExecute() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Chargement");
        progressDialog.setMessage("Veuillez patienter pendant le téléchargement de la liste des mangas.");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

}
