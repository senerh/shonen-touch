package ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import io.github.senerh.shonentouch.R;

public class HelpFragment extends Fragment implements View.OnClickListener {
    private ImageButton mExtendDownloadImageButton;
    private TextView mDownloadDescriptionTextView;
    private ImageButton mExtendRefreshImageButton;
    private TextView mRefreshDescriptionTextView;

    private boolean mIsDownloadCardExtended = false;
    private boolean mIsRefreshCardExtended = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mExtendDownloadImageButton = (ImageButton) view.findViewById(R.id.image_button_download_extend);
        mExtendDownloadImageButton.setOnClickListener(this);
        mExtendRefreshImageButton = (ImageButton) view.findViewById(R.id.image_button_refresh_extend);
        mExtendRefreshImageButton.setOnClickListener(this);

        mDownloadDescriptionTextView = (TextView) view.findViewById(R.id.text_view_download_description);
        mRefreshDescriptionTextView = (TextView) view.findViewById(R.id.text_view_refresh_description);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.image_button_download_extend) :
                mIsDownloadCardExtended = extendOrCloseCard(mIsDownloadCardExtended, mExtendDownloadImageButton, mDownloadDescriptionTextView);
                break;
            case (R.id.image_button_refresh_extend) :
                mIsRefreshCardExtended = extendOrCloseCard(mIsRefreshCardExtended, mExtendRefreshImageButton, mRefreshDescriptionTextView);
                break;
            default:
                break;
        }
    }

//    private void extendOrCloseCard(boolean isExtended, ImageButton imageButton, TextView textView) {
//        LinearInterpolator interpolator = new LinearInterpolator();
//        if (mIsDownloadCardExtended) {
//            ViewCompat.animate(mExtendDownloadImageButton).
//                    rotation(0f).
//                    withLayer().
//                    setDuration(400).
//                    setInterpolator(interpolator).
//                    start();
//            mDownloadDescriptionTextView.setVisibility(View.GONE);
//            mIsDownloadCardExtended = false;
//        } else {
//            ViewCompat.animate(mExtendDownloadImageButton).
//                    rotation(180f).
//                    withLayer().
//                    setDuration(400).
//                    setInterpolator(interpolator).
//                    start();
//            mDownloadDescriptionTextView.setVisibility(View.VISIBLE);
//            mIsDownloadCardExtended = true;
//        }
//    }

    private boolean extendOrCloseCard(boolean isExtended, ImageButton imageButton, TextView textView) {
        LinearInterpolator interpolator = new LinearInterpolator();
        if (isExtended) {
            ViewCompat.animate(imageButton).
                    rotation(0f).
                    withLayer().
                    setDuration(400).
                    setInterpolator(interpolator).
                    start();
            textView.setVisibility(View.GONE);
            return false;
        } else {
            ViewCompat.animate(imageButton).
                    rotation(180f).
                    withLayer().
                    setDuration(400).
                    setInterpolator(interpolator).
                    start();
            textView.setVisibility(View.VISIBLE);
            return true;
        }
    }
}
