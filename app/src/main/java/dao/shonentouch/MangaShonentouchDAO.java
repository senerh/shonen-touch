package dao.shonentouch;

import java.util.List;

import dto.Manga;

public class MangaShonentouchDAO extends AbstractShonentouchDAO<List<Manga>> {

    public MangaShonentouchDAO(InterfaceTaskShonentouchDAO<List<Manga>> interfaceTaskShonentouchDAO) {
        super(interfaceTaskShonentouchDAO);
    }

    @Override
    protected List<Manga> doInBackground(Void... params) {
        String path = "/mangas";
        List<Manga> mangaList = UtilsShonentouchDAO.getList(path, Manga.class);
        return mangaList;
    }

}
