package dao.slack;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class UtilsSlackDAO {

    protected static String call(String method) throws ExceptionSlackDAO {
        return call(new Method(method));
    }

    protected static String call(Method method) throws ExceptionSlackDAO {
        StringBuilder builder = new StringBuilder();

        try {
            URL samples = new URL(method.getUrl());
            URLConnection yc = samples.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                builder.append(inputLine);
            in.close();
            Log.d(UtilsSlackDAO.class.getName(), "The following method was called <~" + method + "~>");
        } catch (IOException e) {
            Log.e(UtilsSlackDAO.class.getName(), "Error while calling the following address <~" + method.getUrl() + "~>.");
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }

        String json = builder.toString();
        isOk(json);
        return json;
    }

    private static void isOk(String json) throws ExceptionSlackDAO {
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (!jsonObject.getBoolean("ok")) {
                String error = jsonObject.getString("error");
                throw new ExceptionSlackDAO("Error <~" + error + "~> in the following json <~" + json + "~>.");
            }
        } catch (JSONException e) {
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }
    }

}
