package dao.slack;

public class SlackDAOException extends Exception {

    public SlackDAOException(String string) {
        super(string);
    }

    public SlackDAOException(String string, Throwable throwable) {
        super(string, throwable);
    }
}
