package dao.shonentouch;

import android.util.Log;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class UtilsShonentouchDAO {

    private static final String API_URL = "http://senerh.xyz:8080/shonen-touch-api";

    public static String get(String path) {
        StringBuilder builder = new StringBuilder();

        try {
            URL samples = new URL(API_URL + path);
            URLConnection yc = samples.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                builder.append(inputLine);
            in.close();
        } catch (IOException e) {
            Log.e(UtilsShonentouchDAO.class.getName(), "Error while calling the following address <~" + API_URL + path + "~>.");
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }

        return builder.toString();
    }

    public static <T> T get(String path, Class<T> type) {
        T t = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String string = get(path);

        try {
            t = objectMapper.readValue(string, type);
        } catch (IOException e) {
            Log.e(UtilsShonentouchDAO.class.getName(), "Error while converting the string <~" + string + "~> into <~" + type.getName() + "~> instance.");
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }

        return t;
    }

    public static <T> List<T> getList(String path, Class<T> type) {
        List<T> list = null;
        ObjectMapper objectMapper = new ObjectMapper();
        JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, type);
        String string = get(path);

        try {
            list = objectMapper.readValue(string, javaType);
        } catch (IOException e) {
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }

        return list;
    }

}
