package dao.shonentouch;

import java.util.List;

import dto.FullPage;

public interface InterfaceFullPageShonentouchService {

    public void onPostExecute(List<FullPage> fullPageList);

    public void onProgressUpdate(FullPage fullPage);

    public void onPreExecute();

}
