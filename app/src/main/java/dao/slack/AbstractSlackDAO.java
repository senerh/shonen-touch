package dao.slack;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class AbstractSlackDAO {

    public static String call(String method) {
        return call(new Method(method));
    }

    public static String call(Method method) {
        StringBuilder builder = new StringBuilder();

        try {
            URL samples = new URL(method.getUrl());
            URLConnection yc = samples.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                builder.append(inputLine);
            in.close();
        } catch (IOException e) {
            Log.e(AbstractSlackDAO.class.getName(), "Error while calling the following address <~" + method.getUrl() + "~>.");
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }

        return builder.toString();
    }

}
