package model.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Johnhetour on 08/07/2017.
 */
public class Manga implements Parcelable {
    public static final Creator<Manga> CREATOR = new Creator<Manga>() {
        @Override
        public Manga createFromParcel(Parcel in) {
            return new Manga(in);
        }

        @Override
        public Manga[] newArray(int size) {
            return new Manga[size];
        }
    };

    private String mSlug;

    private String mName;

    private String mLastScan;

    private String mIconPath;

    private boolean mFavorite;

    public Manga() {
    }

    public Manga(String name, String slug, String lastScan, String iconPath, boolean favorite) {
        mSlug = slug;
        mName = name;
        mLastScan = lastScan;
        mIconPath = iconPath;
        mFavorite = favorite;
    }

    protected Manga(Parcel in) {
        mSlug = in.readString();
        mName = in.readString();
        mLastScan = in.readString();
        mIconPath = in.readString();
        mFavorite = in.readByte() != 0;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getSlug() {
        return mSlug;
    }

    public void setSlug(String slug) {
        mSlug = slug;
    }

    public String getLastScan() {
        return mLastScan;
    }

    public void setLastScan(String lastScan) {
        this.mLastScan = lastScan;
    }

    public String getIconPath() {
        return mIconPath;
    }

    public void setIconPath(String iconPath) {
        this.mIconPath = iconPath;
    }

    public boolean isFavorite() {
        return mFavorite;
    }

    public void setFavorite(boolean favorite) {
        mFavorite = favorite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(mSlug);
        dest.writeString(mName);
        dest.writeString(mLastScan);
        dest.writeString(mIconPath);
        dest.writeByte((byte) (mFavorite ? 1 : 0));
    }
}
