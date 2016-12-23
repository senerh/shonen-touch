package dao.shonentouch;

import android.os.AsyncTask;

import java.util.List;

import activity.FavoritesActivity;
import dto.Manga;

public class MangaShonentouchDAO extends AsyncTask<Void, Manga, List<Manga>> {

    private FavoritesActivity favoritesActivity;

    public MangaShonentouchDAO(FavoritesActivity favoritesActivity) {
        this.favoritesActivity = favoritesActivity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        favoritesActivity.displayOnPreExecute();
    }

    @Override
    protected void onPostExecute(List<Manga> mangaList) {
        favoritesActivity.displayOnPostExecute(mangaList);
    }

    @Override
    protected List<Manga> doInBackground(Void... params) {
        List<Manga> mangaList = UtilsShonentouchDAO.getList("/mangas", Manga.class);
        return mangaList;
    }

}
