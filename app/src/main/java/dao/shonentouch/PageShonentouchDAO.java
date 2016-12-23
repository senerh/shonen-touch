package dao.shonentouch;

import java.util.List;

import activity.InterfaceTaskActivity;
import dto.Manga;
import dto.Page;
import dto.Scan;

public class PageShonentouchDAO extends AbstractShonentouchDAO<List<Page>> {

    private Manga manga;
    private Scan scan;

    public PageShonentouchDAO(
            InterfaceTaskActivity<List<Page>> interfaceTaskActivity,
            Manga manga,
            Scan scan) {
        super(interfaceTaskActivity);
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
