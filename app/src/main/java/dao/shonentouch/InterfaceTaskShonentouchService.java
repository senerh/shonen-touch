package dao.shonentouch;

public interface InterfaceTaskShonentouchService<T> {

    public void displayOnPostExecute(T retrievedData);

    public void displayOnPreExecute();

}
