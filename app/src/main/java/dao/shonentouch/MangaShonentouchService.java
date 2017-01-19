package dao.shonentouch;

import java.util.List;

import dto.Manga;

public class MangaShonentouchService extends AbstractShonentouchDAO<List<Manga>> {

    public MangaShonentouchService(InterfaceTaskShonentouchService<List<Manga>> interfaceTaskShonentouchService) {
        super(interfaceTaskShonentouchService);
    }

    @Override
    protected List<Manga> doInBackground(Void... params) {
        return MethodShonentouchDAO.getMangaList();
    }

}
