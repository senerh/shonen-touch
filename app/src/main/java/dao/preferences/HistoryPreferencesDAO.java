package dao.preferences;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import dto.History;
import dto.Manga;
import dto.Scan;
import dto.UtilsDTO;

/**
 * Created by Franck on 20/01/2017.
 */

public class HistoryPreferencesDAO extends AbstractPreferencesDAO{
    private static final String HISTORY_LIST_KEY = "dao.preferences.HistoryPreferencesDAO.HISTORY_LIST_KEY";

    public HistoryPreferencesDAO(Context context) {
        super(context);
    }

    public List<History> getHistoryList() {
        List<History> historyList;
        String historyListString = readPreferences(HISTORY_LIST_KEY);
        if (historyListString.equals(AbstractPreferencesDAO.NO_PREFERENCE)) {
            historyList = new ArrayList<>();
        } else {
            historyList = UtilsDTO.jsonToObjectList(historyListString, History.class);
        }
        return historyList;
    }

    public void saveHistoryList(List<History> HistoryList) {
        String historyListString = UtilsDTO.objectToJson(HistoryList);
        savePreferences(HISTORY_LIST_KEY, historyListString);
    }

    public void updateHistoryList(Manga manga, Scan scan) {
        History history = new History(manga,scan);
        List<History> historyList = getHistoryList();
        List<History> historyListTemp = new ArrayList<>();
        historyListTemp.add(history);
        for (History current_history:historyList) {
            if (!current_history.equals(history)) {
                historyListTemp.add(current_history);
            }
        }

        String historyListString = UtilsDTO.objectToJson(historyListTemp);
        savePreferences(HISTORY_LIST_KEY, historyListString);
    }
}
