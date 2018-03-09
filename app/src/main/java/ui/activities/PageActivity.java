package ui.activities;

import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
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
import model.entities.Page;

public class PageActivity extends AppCompatActivity {
    public static final String EXTRA_SCAN_ID = "EXTRA_SCAN_ID";
    private ExtendedViewPager extendedViewPager;
    private ProgressBar mProgressBar;
    private ImagePagerAdapter mImagePagerAdapter;
    private List<Page> pagesList;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);
        List<String> pathsImages = new ArrayList<>();
        int scanId = getIntent().getIntExtra(EXTRA_SCAN_ID, -1);
        if (getIntent().getIntExtra(EXTRA_SCAN_ID, -1) != -1) {
            Cursor c = getContentResolver().query(ShonenTouchContract.Page.CONTENT_URI, null, "scanId=?", new String[]{String.valueOf(getIntent().getIntExtra(EXTRA_SCAN_ID, -1))}, null);
            if (c != null) {
                try {
                    c.moveToFirst();
                    while (!c.isAfterLast()) {
                        pathsImages.add(c.getString(c.getColumnIndex(PageColumns.PATH)));
                        c.moveToNext();
                    }
                } finally {
                    c.close();
                }
            }
        }
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(3846);
        decorView.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        extendedViewPager = (ExtendedViewPager) findViewById(R.id.view_pager);
        mImagePagerAdapter = new ImagePagerAdapter(pathsImages);
        extendedViewPager.setAdapter(mImagePagerAdapter);
    }

    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(3846);
        decorView.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//
//        // Checks the orientation of the screen
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            mImagePagerAdapter.photoView.setAdjustViewBounds(true);
//            mImagePagerAdapter.photoView.setScaleType(ScaleType.CENTER_CROP);
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//            mImagePagerAdapter.photoView.setAdjustViewBounds(false);
//            mImagePagerAdapter.photoView.setScaleType(ScaleType.FIT_XY);
//        }
//    }

    class ImagePagerAdapter extends PagerAdapter {
        List<String> imageList;
        TouchImageView photoView;

        ImagePagerAdapter(List<String> imageList) {
            this.imageList = imageList;
        }

        public int getCount() {
            return this.imageList != null ? this.imageList.size() : 0;
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public View instantiateItem(ViewGroup container, int position) {

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                photoView = new TouchImageView(container.getContext(), true);
//                photoView.setAdjustViewBounds(true);
                photoView.setScaleType(ScaleType.CENTER_CROP);
//                System.out.println("##########scroll x : " + photoView.getScrollX());
//                System.out.println("##########scroll y : " + photoView.getScrollY());
//                photoView.setScrollY(0);
            } else {
                photoView = new TouchImageView(container.getContext(), false);
            }
            Options options = new Options();
            options.inPreferredConfig = Config.ARGB_8888;
            photoView.setImageBitmap(BitmapFactory.decodeFile(imageList.get(position), options));
            mProgressBar.setProgress((position * 100) / imageList.size());
            if (position == imageList.size()) {
                mProgressBar.setProgress(100);
            }
            container.addView(photoView, -1, -1);
            return photoView;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
