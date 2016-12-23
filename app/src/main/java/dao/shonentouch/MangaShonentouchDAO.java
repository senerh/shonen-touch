package dao.shonentouch;

import java.util.List;

import activity.InterfaceTaskActivity;
import dto.Manga;

public class MangaShonentouchDAO extends AbstractShonentouchDAO<List<Manga>> {

    public MangaShonentouchDAO(InterfaceTaskActivity<List<Manga>> interfaceTaskActivity) {
        super(interfaceTaskActivity);
    }

    @Override
    protected List<Manga> doInBackground(Void... params) {
        String path = "/mangas";
        List<Manga> mangaList = UtilsShonentouchDAO.getList(path, Manga.class);
        return mangaList;
    }

}
