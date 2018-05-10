package ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
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
    private ImageButton mExtendWrongLanguageScansImageButton;
    private TextView mWrongLanguageScansDescriptionTextView;
    private ImageButton mExtendAddMangaImageButton;
    private TextView mAddMangaDescriptionTextView;
    private ImageButton mExtendDownloadImageButton;
    private TextView mDownloadDescriptionTextView;
    private ImageButton mExtendRefreshImageButton;
    private TextView mRefreshDescriptionTextView;
    private ImageButton mExtendFavoritesImageButton;
    private TextView mFavoritesDescriptionTextView;
    private ImageButton mExtendContactUsImageButton;
    private TextView mContactUsDescriptionTextView;
    private ImageButton mExtendCreditsImageButton;
    private TextView mCreditsDescriptionTextView;

    private boolean mIsWhatIsAScanCardExtended = false;
    private boolean mIsScanMissingCardExtended = false;
    private boolean mIsWrongLanguageScansCardExtended = false;
    private boolean mIsAddMangaCardExtended = false;
    private boolean mIsDownloadCardExtended = false;
    private boolean mIsRefreshCardExtended = false;
    private boolean mIsFavoritesCardExtended = false;
    private boolean mIsContactUsCardExtended = false;
    private boolean mIsCreditsCardExtended = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.card_view_what_is_a_scan).setOnClickListener(this);
        view.findViewById(R.id.card_view_scan_missing).setOnClickListener(this);
        view.findViewById(R.id.card_view_wrong_language_scans).setOnClickListener(this);
        view.findViewById(R.id.card_view_add_manga).setOnClickListener(this);
        view.findViewById(R.id.card_view_download).setOnClickListener(this);
        view.findViewById(R.id.card_view_refresh).setOnClickListener(this);
        view.findViewById(R.id.card_view_favorites).setOnClickListener(this);
        view.findViewById(R.id.card_view_contact_us).setOnClickListener(this);
        view.findViewById(R.id.card_view_credits).setOnClickListener(this);

        mExtendWhatIsAScanImageButton = (ImageButton) view.findViewById(R.id.image_button_what_is_a_scan_extend);
        mExtendWhatIsAScanImageButton.setOnClickListener(this);
        mExtendScanMissingImageButton = (ImageButton) view.findViewById(R.id.image_button_scan_missing_extend);
        mExtendScanMissingImageButton.setOnClickListener(this);
        mExtendWrongLanguageScansImageButton = (ImageButton) view.findViewById(R.id.image_button_wrong_language_scans_extend);
        mExtendWrongLanguageScansImageButton.setOnClickListener(this);
        mExtendAddMangaImageButton = (ImageButton) view.findViewById(R.id.image_button_add_manga_extend);
        mExtendAddMangaImageButton.setOnClickListener(this);
        mExtendDownloadImageButton = (ImageButton) view.findViewById(R.id.image_button_download_extend);
        mExtendDownloadImageButton.setOnClickListener(this);
        mExtendRefreshImageButton = (ImageButton) view.findViewById(R.id.image_button_refresh_extend);
        mExtendRefreshImageButton.setOnClickListener(this);
        mExtendFavoritesImageButton = (ImageButton) view.findViewById(R.id.image_button_favorites_extend);
        mExtendFavoritesImageButton.setOnClickListener(this);
        mExtendContactUsImageButton = (ImageButton) view.findViewById(R.id.image_button_contact_us_extend);
        mExtendContactUsImageButton.setOnClickListener(this);
        mExtendCreditsImageButton = (ImageButton) view.findViewById(R.id.image_button_credits_extend);
        mExtendCreditsImageButton.setOnClickListener(this);

        mWhatIsAScanDescriptionTextView = (TextView) view.findViewById(R.id.text_view_what_is_a_scan_content);
        mScanMissingDescriptionTextView = (TextView) view.findViewById(R.id.text_view_scan_missing_content);
        mWrongLanguageScansDescriptionTextView = (TextView) view.findViewById(R.id.text_view_wrong_language_scans_content);
        mAddMangaDescriptionTextView = (TextView) view.findViewById(R.id.text_view_add_manga_content);
        mDownloadDescriptionTextView = (TextView) view.findViewById(R.id.text_view_download_description);
        mRefreshDescriptionTextView = (TextView) view.findViewById(R.id.text_view_refresh_description);
        mFavoritesDescriptionTextView = (TextView) view.findViewById(R.id.text_view_favorites_description);
        mContactUsDescriptionTextView = (TextView) view.findViewById(R.id.text_view_contact_us_description);
        mCreditsDescriptionTextView = (TextView) view.findViewById(R.id.text_view_credits_description);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.image_button_what_is_a_scan_extend):
            case (R.id.card_view_what_is_a_scan):
                mIsWhatIsAScanCardExtended = extendOrCloseCard(mIsWhatIsAScanCardExtended, mExtendWhatIsAScanImageButton, mWhatIsAScanDescriptionTextView);
                break;
            case (R.id.image_button_scan_missing_extend):
            case (R.id.card_view_scan_missing):
                mIsScanMissingCardExtended = extendOrCloseCard(mIsScanMissingCardExtended, mExtendScanMissingImageButton, mScanMissingDescriptionTextView);
                break;
            case (R.id.image_button_wrong_language_scans_extend):
            case (R.id.card_view_wrong_language_scans):
                mIsWrongLanguageScansCardExtended = extendOrCloseCard(mIsWrongLanguageScansCardExtended, mExtendWrongLanguageScansImageButton, mWrongLanguageScansDescriptionTextView);
                break;
            case (R.id.image_button_add_manga_extend):
            case (R.id.card_view_add_manga):
                mIsAddMangaCardExtended = extendOrCloseCard(mIsAddMangaCardExtended, mExtendAddMangaImageButton, mAddMangaDescriptionTextView);
                break;
            case (R.id.image_button_download_extend):
            case (R.id.card_view_download):
                mIsDownloadCardExtended = extendOrCloseCard(mIsDownloadCardExtended, mExtendDownloadImageButton, mDownloadDescriptionTextView);
                break;
            case (R.id.image_button_refresh_extend):
            case (R.id.card_view_refresh):
                mIsRefreshCardExtended = extendOrCloseCard(mIsRefreshCardExtended, mExtendRefreshImageButton, mRefreshDescriptionTextView);
                break;
            case (R.id.image_button_favorites_extend):
            case (R.id.card_view_favorites):
                mIsFavoritesCardExtended = extendOrCloseCard(mIsFavoritesCardExtended, mExtendFavoritesImageButton, mFavoritesDescriptionTextView);
                break;
            case (R.id.image_button_contact_us_extend):
            case (R.id.card_view_contact_us):
                mIsContactUsCardExtended = extendOrCloseCard(mIsContactUsCardExtended, mExtendContactUsImageButton, mContactUsDescriptionTextView);
                break;
            case (R.id.image_button_credits_extend):
            case (R.id.card_view_credits):
                mIsCreditsCardExtended = extendOrCloseCard(mIsCreditsCardExtended, mExtendCreditsImageButton, mCreditsDescriptionTextView);
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
