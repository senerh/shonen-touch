package dto;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import dao.preferences.AbstractPreferencesDAO;

public class UtilsDTO {

    public static <T> T jsonToObject(String json, Class<T> type) {
        T t = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            t = objectMapper.readValue(json, type);
        } catch (IOException e) {
            Log.e(AbstractPreferencesDAO.class.getName(), "Error while converting the string <~" + json + "~> into <~" + type.getName() + "~> instance.");
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }

        return t;
    }

    public static <T> List<T> jsonToObjectList(String json, Class<T> type) {
        List<T> list = null;
        ObjectMapper objectMapper = new ObjectMapper();
        JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, type);

        try {
            list = objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            Log.e(AbstractPreferencesDAO.class.getName(), "Error while converting the string <~" + json + "~> into list of <~" + type.getName() + "~> instance.");
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }

        return list;
    }

    public static String objectToJson(Object object) {
        String json = "";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            json = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            Log.e(AbstractPreferencesDAO.class.getName(), "Error while trying to get json from the following object <~" + object + "~>");
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }
        return json;
    }
}
