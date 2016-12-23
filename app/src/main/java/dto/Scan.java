package dto;

import android.os.Parcel;
import android.os.Parcelable;

public class Scan implements Parcelable {

    private String num;

    public Scan() {

    }

    public Scan(String num) {
        this.num = num;
    }

    private Scan(Parcel in) {
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
        if (!(o instanceof Scan)) return false;

        Scan scan = (Scan) o;

        return num.equals(scan.num);
    }

    @Override
    public int hashCode() {
        return num.hashCode();
    }

    @Override
    public String toString() {
        return "Scan{" +
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

    public static final Parcelable.Creator<Scan> CREATOR = new Parcelable.Creator<Scan>() {
        @Override
        public Scan createFromParcel(Parcel source) {
            return new Scan(source);
        }

        @Override
        public Scan[] newArray(int size) {
            return new Scan[size];
        }
    };
}
