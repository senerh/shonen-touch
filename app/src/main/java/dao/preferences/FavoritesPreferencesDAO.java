package dao.preferences;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import dto.Manga;
import dto.UtilsDTO;

public class FavoritesPreferencesDAO extends AbstractPreferencesDAO {

    private static final String FAVORITE_MANGA_LIST_KEY = "dao.preferences.FavoritesPreferencesDAO.FAVORITE_MANGA_LIST_KEY";

    public FavoritesPreferencesDAO(Context context) {
        super(context);
    }

    public List<Manga> getFavoriteMangaList() {
        List<Manga> favoriteMangaList;
        String favoriteMangaListString = readPreferences(FAVORITE_MANGA_LIST_KEY);
        if (favoriteMangaListString.equals(AbstractPreferencesDAO.NO_PREFERENCE)) {
            favoriteMangaList = new ArrayList<>();
        } else {
            favoriteMangaList = UtilsDTO.jsonToObjectList(favoriteMangaListString, Manga.class);
        }
        return favoriteMangaList;
    }

    public void saveFavoriteMangaList(List<Manga> favoriteMangaList) {
        String favoriteMangaListString = UtilsDTO.objectToJson(favoriteMangaList);
        savePreferences(FAVORITE_MANGA_LIST_KEY, favoriteMangaListString);
    }

}
