package dao.shonentouch;

import android.os.AsyncTask;

public abstract class AbstractShonentouchDAO<T> extends AsyncTask<Void, Void, T> {

    private InterfaceTaskShonentouchDAO<T> interfaceTaskShonentouchDAO;

    public AbstractShonentouchDAO(InterfaceTaskShonentouchDAO<T> interfaceTaskShonentouchDAO) {
        this.interfaceTaskShonentouchDAO = interfaceTaskShonentouchDAO;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        interfaceTaskShonentouchDAO.displayOnPreExecute();
    }

    @Override
    protected void onPostExecute(T retrivedData) {
        interfaceTaskShonentouchDAO.displayOnPostExecute(retrivedData);
    }
}
