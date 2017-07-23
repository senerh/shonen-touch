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

    private String mPath;

    public Page() {
    }

    public Page(String name, String slug) {
        mPath = name;
    }

    protected Page(Parcel in) {
        mPath = in.readString();
    }

    public String getName() {
        return mPath;
    }

    public void setName(String name) {
        mPath = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(mPath);
    }
}
