package dao.shonentouch;

import java.util.Collections;
import java.util.List;

import dto.Manga;
import dto.Scan;

public class ScanShonentouchService extends AbstractShonentouchDAO<List<Scan>> {

    private Manga manga;

    public ScanShonentouchService(InterfaceTaskShonentouchService<List<Scan>> interfaceTaskShonentouchService, Manga manga) {
        super(interfaceTaskShonentouchService);
        this.manga = manga;
    }

    @Override
    protected List<Scan> doInBackground(Void... params) {
        List<Scan> scanList = MethodShonentouchDAO.getScanList(manga);
        if (scanList != null) {
            Collections.reverse(scanList);
        }
        return scanList;
    }
}
