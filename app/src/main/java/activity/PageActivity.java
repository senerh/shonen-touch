package activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.shonen.shonentouch.R;

import java.util.ArrayList;
import java.util.List;

import adapter.SlidePageAdapter;
import dao.shonentouch.FullPageShonentouchService;
import dao.shonentouch.InterfaceFullPageShonentouchService;
import dto.FullPage;
import dto.Manga;
import dto.Scan;


public class PageActivity extends FragmentActivity implements InterfaceFullPageShonentouchService {

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private List<FullPage> listFullPage;
    private Manga manga;
    private Scan scan;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);

        Bundle b = getIntent().getExtras();

        manga = b.getParcelable("manga");
        scan = b.getParcelable("scan");

        if (savedInstanceState == null) {
            listFullPage = new ArrayList<>();
            new FullPageShonentouchService(
                    this,
                    manga,
                    scan).execute();
        }
        else {
            listFullPage = savedInstanceState.getParcelableArrayList("fullPageList");
        }

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new SlidePageAdapter(getSupportFragmentManager(), listFullPage);
        mPager.setAdapter(mPagerAdapter);
    }


    @Override
    public void onPostExecute(List<FullPage> fullPageList) {
        progressDialog.dismiss();
        if (fullPageList == null) {
            Toast.makeText(this.getApplicationContext(), "Aucune page n'a été trouvée, vérifiez votre connexion internet.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onProgressUpdate(FullPage fullPage) {
        progressDialog.dismiss();
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
}
