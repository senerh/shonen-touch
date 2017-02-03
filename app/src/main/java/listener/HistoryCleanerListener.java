package listener;

import android.view.View;

import dao.preferences.HistoryPreferencesDAO;
import fragment.HistoryFragment;

public class HistoryCleanerListener implements View.OnClickListener {

    private HistoryFragment historyFragment;

    public HistoryCleanerListener(HistoryFragment historyFragment) {
        this.historyFragment = historyFragment;
    }

    @Override
    public void onClick(View v) {
        new HistoryPreferencesDAO(historyFragment.getContext()).clean();
        historyFragment.getHistoryList().clear();
        historyFragment.getHistoryAdapter().notifyDataSetChanged();
        historyFragment.getEmptyTextView().setVisibility(View.VISIBLE);
    }
}
