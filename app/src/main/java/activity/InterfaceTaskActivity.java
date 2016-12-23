package activity;

public interface InterfaceTaskActivity<T> {

    public void displayOnPostExecute(T retrievedData);

    public void displayOnPreExecute();

}
