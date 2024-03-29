package ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.facebook.stetho.Stetho;

import io.github.senerh.shonentouch.BuildConfig;
import io.github.senerh.shonentouch.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this); // for debug only, don't worry
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_manga_list_items, menu);
        return true;
    }
}
