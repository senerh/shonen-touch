package listener;

import android.view.View;
import android.widget.Toast;

import java.util.List;

import dao.preferences.HistoryPreferencesDAO;
import dto.History;
import fragment.HistoryFragment;

/**
 * Created by Franck on 03/02/2017.
 */

public class HistoryCleanerListener implements View.OnClickListener{
    private HistoryFragment fragment;

    public HistoryCleanerListener(HistoryFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onClick(View v) {
        List<History> history = this.fragment.getHistoryList();
        history.clear();
        new HistoryPreferencesDAO(fragment.getContext()).saveHistoryList(history);
        fragment.getHistoryAdapter().notifyDataSetChanged();
        Toast.makeText(fragment.getContext(), "Historique supprimé", Toast.LENGTH_LONG).show();
        fragment.getEmptyTextView().setText("Historique vide");
    }
}
