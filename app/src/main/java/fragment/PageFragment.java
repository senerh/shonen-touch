package fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import dto.FullPage;
import io.github.senerh.shonentouch.R;
import uk.co.senab.photoview.PhotoViewAttacher;


public class PageFragment extends Fragment {

    public static final String FULL_PAGE_KEY = "fragment.FULL_PAGE_KEY";
    private FullPage mFullPage;
    private PhotoViewAttacher mAttacher;

    public PageFragment() {

    }

    public static PageFragment create(FullPage fullPage) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putParcelable(FULL_PAGE_KEY, fullPage);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFullPage = getArguments().getParcelable(FULL_PAGE_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_page, container, false);

        ImageView imageMangaView = (ImageView)rootView.findViewById(R.id.image_manga);
        Bitmap bitmap = bytesToBitmap(mFullPage.getImage().getImageBytes());
        imageMangaView.setImageBitmap(bitmap);

        mAttacher = new PhotoViewAttacher(imageMangaView);

        View decorView = getActivity().getWindow().getDecorView();

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        decorView.setBackgroundColor(Color.BLACK);

        return rootView;
    }

    private static Bitmap bytesToBitmap(byte[] bytes) {
        System.gc();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
