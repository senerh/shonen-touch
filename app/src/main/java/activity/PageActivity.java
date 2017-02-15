package activity;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import adapter.SlidePageAdapter;
import dao.preferences.HistoryPreferencesDAO;
import dao.shonentouch.InterfaceFullPageShonentouchService;
import dto.FullPage;
import dto.Manga;
import dto.Scan;
import fragment.FullPageTaskFragment;
import fragment.ScanFragment;
import io.github.senerh.shonentouch.R;
import listener.OnSwipeOutListener;
import view.CustomViewPager;


public class PageActivity extends FragmentActivity implements InterfaceFullPageShonentouchService {

    private static final String FULL_PAGE_TASK_FRAGMENT = "activity.PageActivity.FULL_PAGE_TASK_FRAGMENT";
    private static final String IS_LOADED_TAG = "activity.PageActivity.IS_LOADED_TAG";
    private static final String FULL_PAGE_LIST_TAG = "activity.PageActivity.FULL_PAGE_LIST_TAG";

    private CustomViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private List<FullPage> listFullPage;
    private Manga manga;
    private Scan scan;
    private List<Scan> scanList;
    private ProgressDialog progressDialog;
    private FullPageTaskFragment fullPageTaskFragment;
    private boolean isLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);

        Bundle b = getIntent().getExtras();

        manga = b.getParcelable(ScanFragment.ID_MANGA_PARCELABLE);
        scan = b.getParcelable(ScanFragment.ID_SCAN_PARCELABLE);
        scanList = b.getParcelableArrayList(ScanFragment.ID_SCAN_LIST);

        FragmentManager fragmentManager = getFragmentManager();
        fullPageTaskFragment = (FullPageTaskFragment) fragmentManager.findFragmentByTag(FULL_PAGE_TASK_FRAGMENT);
        if (fullPageTaskFragment == null) {
            fullPageTaskFragment = FullPageTaskFragment.newInstance(manga, scan);
            fragmentManager.beginTransaction().add(fullPageTaskFragment, FULL_PAGE_TASK_FRAGMENT).commit();
        }

        if (savedInstanceState == null) {
            listFullPage = new ArrayList<>();
            isLoaded = false;
        } else {
            listFullPage = savedInstanceState.getParcelableArrayList(FULL_PAGE_LIST_TAG);
            isLoaded = savedInstanceState.getBoolean(IS_LOADED_TAG);
        }

        mPagerAdapter = new SlidePageAdapter(getSupportFragmentManager(), listFullPage);
        mPager = (CustomViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnSwipeOutListener(new OnSwipeOutListener(this));
        mPager.setIsLoaded(isLoaded);

        new HistoryPreferencesDAO(this).addEntry(manga, scan);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onPostExecute(List<FullPage> fullPageList) {
        if (progressDialog != null && progressDialog.isShowing()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            progressDialog.dismiss();
        }
        if (fullPageList == null || fullPageList.isEmpty()) {
            Toast.makeText(this.getApplicationContext(), "Aucune page n'a été trouvée, vérifiez votre connexion internet.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            isLoaded = true;
            mPager.setIsLoaded(isLoaded);
        }
    }

    @Override
    public void onProgressUpdate(FullPage fullPage) {
        if (progressDialog != null && progressDialog.isShowing()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            progressDialog.dismiss();
        }
        this.listFullPage.add(fullPage);
        mPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPreExecute() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Chargement");
        progressDialog.setMessage("Veuillez patienter pendant le chargement de la page.");
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<FullPage> fullPagesArrayList = new ArrayList<>(listFullPage);
        outState.putParcelableArrayList(FULL_PAGE_LIST_TAG, fullPagesArrayList);
        outState.putBoolean(IS_LOADED_TAG, isLoaded);
    }

    public void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public Manga getManga() {
        return manga;
    }

    public Scan getScan() {
        return scan;
    }

    public List<Scan> getScanList() {
        return scanList;
    }
}

