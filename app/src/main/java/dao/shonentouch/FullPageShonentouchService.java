package dao.shonentouch;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import dto.FullPage;
import dto.Image;
import dto.Manga;
import dto.Page;
import dto.Scan;

public class FullPageShonentouchService extends AsyncTask<Void, FullPage, List<FullPage>> {

    private InterfaceFullPageShonentouchService interfaceFullPageShonentouchService;
    private Manga manga;
    private Scan scan;

    public FullPageShonentouchService(
            InterfaceFullPageShonentouchService interfaceFullPageShonentouchService,
            Manga manga,
            Scan scan) {
        this.interfaceFullPageShonentouchService = interfaceFullPageShonentouchService;
        this.manga = manga;
        this.scan = scan;
    }

    @Override
    protected List<FullPage> doInBackground(Void... params) {
        List<Page> pageList = MethodShonentouchDAO.getPageList(manga, scan);
        List<FullPage> fullPageList = new ArrayList<>();
        for (Page page : pageList) {
            Image image = MethodShonentouchDAO.getImage(manga, scan, page);
            FullPage fullPage = new FullPage(page, image);
            fullPageList.add(fullPage);
            publishProgress(fullPage);
            if (isCancelled()) {
                break;
            }
        }
        return fullPageList;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        interfaceFullPageShonentouchService.onPreExecute();
    }

    @Override
    protected void onProgressUpdate (FullPage... progress) {
        interfaceFullPageShonentouchService.onProgressUpdate(progress[0]);
    }

    @Override
    protected void onPostExecute(List<FullPage> retrivedData) {
        interfaceFullPageShonentouchService.onPostExecute(retrivedData);
    }

}
