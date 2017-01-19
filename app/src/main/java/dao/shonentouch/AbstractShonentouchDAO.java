package dao.shonentouch;

import android.os.AsyncTask;

public abstract class AbstractShonentouchDAO<T> extends AsyncTask<Void, Void, T> {

    private InterfaceTaskShonentouchService<T> interfaceTaskShonentouchService;

    public AbstractShonentouchDAO(InterfaceTaskShonentouchService<T> interfaceTaskShonentouchService) {
        this.interfaceTaskShonentouchService = interfaceTaskShonentouchService;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        interfaceTaskShonentouchService.displayOnPreExecute();
    }

    @Override
    protected void onPostExecute(T retrivedData) {
        interfaceTaskShonentouchService.displayOnPostExecute(retrivedData);
    }
}
