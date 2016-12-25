package dao.shonentouch;

import java.util.List;

import dto.Manga;
import dto.Page;
import dto.Scan;

public class PageShonentouchDAO extends AbstractShonentouchDAO<List<Page>> {

    private Manga manga;
    private Scan scan;

    public PageShonentouchDAO(
            InterfaceTaskShonentouchDAO<List<Page>> interfaceTaskShonentouchDAO,
            Manga manga,
            Scan scan) {
        super(interfaceTaskShonentouchDAO);
        this.manga = manga;
        this.scan = scan;
    }

    @Override
    protected List<Page> doInBackground(Void... params) {
        String path = "/mangas/" + manga.getSlug() + "/scans" + scan.getNum();
        List<Page> pageList = UtilsShonentouchDAO.getList(path, Page.class);
        return pageList;
    }
}
