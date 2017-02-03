package dao.preferences;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import dto.History;
import dto.Manga;
import dto.Scan;
import dto.UtilsDTO;

public class HistoryPreferencesDAO extends AbstractPreferencesDAO {

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

    public void addEntry(Manga manga, Scan scan) {
        History history = new History(manga,scan);
        List<History> historyList = getHistoryList();
        List<History> newHistoryList = new ArrayList<>();
        newHistoryList.add(history);
        for (History currentHistory : historyList) {
            if (!currentHistory.equals(history)) {
                newHistoryList.add(currentHistory);
            }
        }

        saveHistoryList(newHistoryList);
    }

    public void clean() {
        saveHistoryList(new ArrayList<History>());
    }

    private void saveHistoryList(List<History> historyList) {
        String historyListString = UtilsDTO.objectToJson(historyList);
        savePreferences(HISTORY_LIST_KEY, historyListString);
    }
}
