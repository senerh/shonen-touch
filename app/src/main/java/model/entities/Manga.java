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

    public Manga() {
    }

    public Manga(String name, String slug) {
        mSlug = slug;
        mName = name;
    }

    protected Manga(Parcel in) {
        mSlug = in.readString();
        mName = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(mSlug);
        dest.writeString(mName);
    }
}
