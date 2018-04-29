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
    private ImageButton mExtendWhatIsAScanImageButton;
    private TextView mWhatIsAScanDescriptionTextView;
    private ImageButton mExtendScanMissingImageButton;
    private TextView mScanMissingDescriptionTextView;
    private ImageButton mExtendAddMangaImageButton;
    private TextView mAddMangaDescriptionTextView;
    private ImageButton mExtendDownloadImageButton;
    private TextView mDownloadDescriptionTextView;
    private ImageButton mExtendRefreshImageButton;
    private TextView mRefreshDescriptionTextView;
    private ImageButton mExtendContactUsImageButton;
    private TextView mContactUsDescriptionTextView;

    private boolean mIsWhatIsAScanCardExtended = false;
    private boolean mIsScanMissingCardExtended = false;
    private boolean mIsAddMangaCardExtended = false;
    private boolean mIsDownloadCardExtended = false;
    private boolean mIsRefreshCardExtended = false;
    private boolean mIsContactUsCardExtended = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mExtendWhatIsAScanImageButton = (ImageButton) view.findViewById(R.id.image_button_what_is_a_scan_extend);
        mExtendWhatIsAScanImageButton.setOnClickListener(this);
        mExtendScanMissingImageButton = (ImageButton) view.findViewById(R.id.image_button_scan_missing_extend);
        mExtendScanMissingImageButton.setOnClickListener(this);
        mExtendAddMangaImageButton = (ImageButton) view.findViewById(R.id.image_button_add_manga_extend);
        mExtendAddMangaImageButton.setOnClickListener(this);
        mExtendDownloadImageButton = (ImageButton) view.findViewById(R.id.image_button_download_extend);
        mExtendDownloadImageButton.setOnClickListener(this);
        mExtendRefreshImageButton = (ImageButton) view.findViewById(R.id.image_button_refresh_extend);
        mExtendRefreshImageButton.setOnClickListener(this);
        mExtendContactUsImageButton = (ImageButton) view.findViewById(R.id.image_button_contact_us_extend);
        mExtendContactUsImageButton.setOnClickListener(this);

        mWhatIsAScanDescriptionTextView = (TextView) view.findViewById(R.id.text_view_what_is_a_scan_content);
        mScanMissingDescriptionTextView = (TextView) view.findViewById(R.id.text_view_scan_missing_content);
        mAddMangaDescriptionTextView = (TextView) view.findViewById(R.id.text_view_add_manga_content);
        mDownloadDescriptionTextView = (TextView) view.findViewById(R.id.text_view_download_description);
        mRefreshDescriptionTextView = (TextView) view.findViewById(R.id.text_view_refresh_description);
        mContactUsDescriptionTextView = (TextView) view.findViewById(R.id.text_view_contact_us_description);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.image_button_what_is_a_scan_extend) :
                mIsWhatIsAScanCardExtended = extendOrCloseCard(mIsWhatIsAScanCardExtended, mExtendWhatIsAScanImageButton, mWhatIsAScanDescriptionTextView);
                break;
            case (R.id.image_button_scan_missing_extend) :
                mIsScanMissingCardExtended = extendOrCloseCard(mIsScanMissingCardExtended, mExtendScanMissingImageButton, mScanMissingDescriptionTextView);
                break;
            case (R.id.image_button_add_manga_extend) :
                mIsAddMangaCardExtended = extendOrCloseCard(mIsAddMangaCardExtended, mExtendAddMangaImageButton, mAddMangaDescriptionTextView);
                break;
            case (R.id.image_button_download_extend) :
                mIsDownloadCardExtended = extendOrCloseCard(mIsDownloadCardExtended, mExtendDownloadImageButton, mDownloadDescriptionTextView);
                break;
            case (R.id.image_button_refresh_extend) :
                mIsRefreshCardExtended = extendOrCloseCard(mIsRefreshCardExtended, mExtendRefreshImageButton, mRefreshDescriptionTextView);
                break;
            case (R.id.image_button_contact_us_extend) :
                mIsContactUsCardExtended = extendOrCloseCard(mIsContactUsCardExtended, mExtendContactUsImageButton, mContactUsDescriptionTextView);
                break;
            default:
                break;
        }
    }

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
