package activity;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.shonen.shonentouch.R;

import java.util.ArrayList;
import java.util.List;

import adapter.SlidePageAdapter;
import dao.shonentouch.InterfaceFullPageShonentouchService;
import dto.FullPage;
import dto.Manga;
import dto.Scan;
import fragment.FullPageTaskFragment;


public class PageActivity extends FragmentActivity implements InterfaceFullPageShonentouchService {

    private static final String FULL_PAGE_TASK_FRAGMENT = "FULL_PAGE_TASK_FRAGMENT";

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private List<FullPage> listFullPage;
    private Manga manga;
    private Scan scan;
    private ProgressDialog progressDialog;
    private FullPageTaskFragment fullPageTaskFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);

        Bundle b = getIntent().getExtras();

        manga = b.getParcelable("manga");
        scan = b.getParcelable("scan");

        FragmentManager fragmentManager = getFragmentManager();
        fullPageTaskFragment = (FullPageTaskFragment) fragmentManager.findFragmentByTag(FULL_PAGE_TASK_FRAGMENT);
        if (fullPageTaskFragment == null) {
            fullPageTaskFragment = FullPageTaskFragment.newInstance(manga, scan);
            fragmentManager.beginTransaction().add(fullPageTaskFragment, FULL_PAGE_TASK_FRAGMENT).commit();
        }

        if (savedInstanceState == null) {
            listFullPage = new ArrayList<>();
        }
        else {
            listFullPage = savedInstanceState.getParcelableArrayList("fullPageList");
        }

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new SlidePageAdapter(getSupportFragmentManager(), listFullPage);
        mPager.setAdapter(mPagerAdapter);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onPostExecute(List<FullPage> fullPageList) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (fullPageList == null) {
            Toast.makeText(this.getApplicationContext(), "Aucune page n'a été trouvée, vérifiez votre connexion internet.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onProgressUpdate(FullPage fullPage) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        this.listFullPage.add(fullPage);
        mPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPreExecute() {
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
        outState.putParcelableArrayList("fullPageList", fullPagesArrayList);
    }

    public void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
