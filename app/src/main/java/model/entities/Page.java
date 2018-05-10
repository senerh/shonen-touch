package model.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Johnhetour on 08/07/2017.
 */

public class Page implements Parcelable {
    public static final Creator<Page> CREATOR = new Creator<Page>() {
        @Override
        public Page createFromParcel(Parcel in) {
            return new Page(in);
        }

        @Override
        public Page[] newArray(int size) {
            return new Page[size];
        }
    };

    private String mName;

    private String mPath;

    public Page() {
    }

    public Page(String name, String path) {
        mPath = path;
        mName = name;
    }

    protected Page(Parcel in) {
        mPath = in.readString();
        mName = in.readString();
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(mPath);
        dest.writeString(mName);
    }
}
