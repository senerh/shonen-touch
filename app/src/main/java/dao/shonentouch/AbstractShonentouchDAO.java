package dao.shonentouch;

import android.os.AsyncTask;

import activity.InterfaceTaskActivity;

public abstract class AbstractShonentouchDAO<T> extends AsyncTask<Void, Void, T> {

    private InterfaceTaskActivity<T> interfaceTaskActivity;

    public AbstractShonentouchDAO(InterfaceTaskActivity<T> interfaceTaskActivity) {
        this.interfaceTaskActivity = interfaceTaskActivity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        interfaceTaskActivity.displayOnPreExecute();
    }

    @Override
    protected void onPostExecute(T retrivedData) {
        interfaceTaskActivity.displayOnPostExecute(retrivedData);
    }
}
