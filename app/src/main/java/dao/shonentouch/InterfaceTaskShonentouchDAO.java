package dao.shonentouch;

public interface InterfaceTaskShonentouchDAO<T> {

    public void displayOnPostExecute(T retrievedData);

    public void displayOnPreExecute();

}
