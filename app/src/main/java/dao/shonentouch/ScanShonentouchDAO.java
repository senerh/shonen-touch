package dao.shonentouch;

import java.util.List;

import activity.InterfaceTaskActivity;
import dto.Manga;
import dto.Scan;

public class ScanShonentouchDAO extends AbstractShonentouchDAO<List<Scan>> {

    private Manga manga;

    public ScanShonentouchDAO(InterfaceTaskActivity<List<Scan>> interfaceTaskActivity, Manga manga) {
        super(interfaceTaskActivity);
        this.manga = manga;
    }

    @Override
    protected List<Scan> doInBackground(Void... params) {
        String path = "/mangas/" + manga.getSlug() + "/scans";
        List<Scan> scanList = UtilsShonentouchDAO.getList(path, Scan.class);
        return scanList;
    }
}
