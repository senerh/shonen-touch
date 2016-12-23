package dto;

import android.os.Parcel;
import android.os.Parcelable;

public class Page implements Parcelable {

    private String num;

    public Page() {

    }

    public Page(String num) {
        this.num = num;
    }

    private Page(Parcel in) {
        num = in.readString();
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Page)) return false;

        Page scan = (Page) o;

        return num.equals(scan.num);
    }

    @Override
    public int hashCode() {
        return num.hashCode();
    }

    @Override
    public String toString() {
        return "Page{" +
                "num='" + num + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(num);
    }

    public static final Creator<Page> CREATOR = new Creator<Page>() {
        @Override
        public Page createFromParcel(Parcel source) {
            return new Page(source);
        }

        @Override
        public Page[] newArray(int size) {
            return new Page[size];
        }
    };
}
