package ui.activities;

import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import ui.views.ExtendedViewPager;
import ui.views.TouchImageView;
import java.util.ArrayList;
import java.util.List;

import io.github.senerh.shonentouch.R;
import model.database.ShonenTouchContract;
import model.database.ShonenTouchContract.PageColumns;

public class PageActivity extends AppCompatActivity {
    public static final String EXTRA_SCAN_ID = "EXTRA_SCAN_ID";
    private ProgressBar mProgressBar;
    private List<String> mImagesPathsList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);
        mImagesPathsList = new ArrayList<>();
        int scanId = getIntent().getIntExtra(EXTRA_SCAN_ID, -1);
        if (scanId != -1) {
            Cursor c = getContentResolver().query(ShonenTouchContract.Page.CONTENT_URI, null, PageColumns.SCAN_ID + "=?", new String[]{String.valueOf(scanId)}, null);
            if (c != null) {
                try {
                    c.moveToFirst();
                    while (!c.isAfterLast()) {
                        mImagesPathsList.add(c.getString(c.getColumnIndex(PageColumns.PATH)));
                        c.moveToNext();
                    }
                } finally {
                    c.close();
                }
            }
        }
        setImmersiveScreen();
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        ExtendedViewPager extendedViewPager = (ExtendedViewPager) findViewById(R.id.view_pager);
        ImagePagerAdapter mImagePagerAdapter = new ImagePagerAdapter();
        extendedViewPager.setAdapter(mImagePagerAdapter);
        extendedViewPager.addOnPageChangeListener(new DetailOnPageChangeListener());
        mProgressBar.setProgress(100 / mImagesPathsList.size());
    }

    protected void onResume() {
        super.onResume();
        setImmersiveScreen();
    }

    private void setImmersiveScreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE);
        decorView.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
    }

    class ImagePagerAdapter extends PagerAdapter {
        TouchImageView mPhotoView;

        ImagePagerAdapter() {}

        public int getCount() {
            return mImagesPathsList != null ? mImagesPathsList.size() : 0;
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public View instantiateItem(ViewGroup container, int position) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mPhotoView = new TouchImageView(container.getContext(), true);
                mPhotoView.setScaleType(ScaleType.CENTER_CROP);
            } else {
                mPhotoView = new TouchImageView(container.getContext(), false);
            }

            Options options = new Options();
            options.inPreferredConfig = Config.ARGB_8888;
            mPhotoView.setImageBitmap(BitmapFactory.decodeFile(mImagesPathsList.get(position), options));
            container.addView(mPhotoView, -1, -1);
            return mPhotoView;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    /**
     * Used to have the current element position to display progress bar
     */
    public class DetailOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            mProgressBar.setProgress(((position + 1) * 100) / mImagesPathsList.size());
            if (position == mImagesPathsList.size()) {
                mProgressBar.setProgress(100);
            }
        }
    }
}
