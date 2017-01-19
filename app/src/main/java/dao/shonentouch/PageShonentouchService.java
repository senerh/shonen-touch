package dao.shonentouch;

import java.util.List;

import dto.Manga;
import dto.Page;
import dto.Scan;

public class PageShonentouchService extends AbstractShonentouchDAO<List<Page>> {

    private Manga manga;
    private Scan scan;

    public PageShonentouchService(
            InterfaceTaskShonentouchService<List<Page>> interfaceTaskShonentouchService,
            Manga manga,
            Scan scan) {
        super(interfaceTaskShonentouchService);
        this.manga = manga;
        this.scan = scan;
    }

    @Override
    protected List<Page> doInBackground(Void... params) {
        return MethodShonentouchDAO.getPageList(manga, scan);
    }
}
