package listener;

import android.view.View;

import java.util.List;

import dto.History;
import dto.Manga;
import dto.Scan;
import fragment.ScanFragment;

public class LastScanListener implements View.OnClickListener {

    private final ScanFragment scanFragment;
    private Manga manga;
    private Scan scan;
    private List<Scan> scanList;

    public LastScanListener(ScanFragment scanFragment, History history, List<Scan> scanList) {
        this.scanFragment = scanFragment;
        manga = history.getManga();
        scan = history.getScan();
        this.scanList = scanList;
    }

    @Override
    public void onClick(View v) {
        scanFragment.startPageActivity(manga, scan, scanList);
    }
}
