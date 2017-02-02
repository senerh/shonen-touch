package listener;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import activity.PageActivity;
import dto.Scan;
import fragment.ScanFragment;
import widget.CustomViewPager;

public class OnSwipeOutListener implements CustomViewPager.OnSwipeOutListener {

    private PageActivity pageActivity;
    private Scan nextScan;
    private boolean isAlreadySwiped;

    public OnSwipeOutListener(PageActivity pageActivity) {
        this.pageActivity = pageActivity;
        isAlreadySwiped = false;

        int scanIndex = pageActivity.getScanList().indexOf(pageActivity.getScan());
        if (scanIndex > 0) {
            nextScan = pageActivity.getScanList().get(scanIndex - 1);
        }
    }

    @Override
    public void onSwipeOutAtStart() {
        String msg = "Vous lisez le scan " + pageActivity.getScan().getNum() + " du manga " + pageActivity.getManga().getName();
        Toast.makeText(pageActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSwipeOutAtEnd() {
        if (!isAlreadySwiped) {
            isAlreadySwiped = true;
            String msg = "Glissez une seconde fois pour passer au scan suivant";
            Toast.makeText(pageActivity, msg, Toast.LENGTH_SHORT).show();
        } else if (nextScan == null) {
            String msg = "Vous venez de terminer le dernier scan du manga " + pageActivity.getManga().getName();
            Toast.makeText(pageActivity, msg, Toast.LENGTH_SHORT).show();
        } else {
            Intent myIntent = new Intent(pageActivity, PageActivity.class);

            Bundle b = new Bundle();
            b.putParcelable(ScanFragment.ID_MANGA_PARCELABLE, pageActivity.getManga());
            b.putParcelable(ScanFragment.ID_SCAN_PARCELABLE, nextScan);
            b.putParcelableArrayList(ScanFragment.ID_SCAN_LIST, new ArrayList<>(pageActivity.getScanList()));
            myIntent.putExtras(b);

            pageActivity.startActivity(myIntent);
            pageActivity.finish();
        }
    }
}
