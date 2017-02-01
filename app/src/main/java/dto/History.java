package dto;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Franck on 20/01/2017.
 */

public class History implements Parcelable {

    private Manga manga;
    private Scan scan;
    private Page page;

    public History(){

    }

    public History(Manga manga, Scan scan, Page page){
        this.manga = manga;
        this.scan = scan;
        this.page = page;
    }
    public History(Manga manga, Scan scan){
        this.manga = manga;
        this.scan = scan;
    }

    protected History(Parcel in) {
        manga = in.readParcelable(Manga.class.getClassLoader());
        scan = in.readParcelable(Scan.class.getClassLoader());
        page = in.readParcelable(Page.class.getClassLoader());
    }

    public Manga getManga() {
        return manga;
    }
    public void setManga(Manga manga) {
        this.manga = manga;
    }
    public Scan getScan() {
        return scan;
    }
    public void setScan(Scan scan) {
        this.scan = scan;
    }
    public Page getPage() {
        return page;
    }
    public void setPage(Page page) {
        this.page = page;
    }

    public static final Creator<History> CREATOR = new Creator<History>() {
        @Override
        public History createFromParcel(Parcel in) {
            return new History(in);
        }

        @Override
        public History[] newArray(int size) {
            return new History[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(manga, flags);
        dest.writeParcelable(scan, flags);
        dest.writeParcelable(page, flags);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        History history = (History) o;

        if (manga != null ? !manga.equals(history.manga) : history.manga != null) return false;
        if (scan != null ? !scan.equals(history.scan) : history.scan != null) return false;
        return page != null ? page.equals(history.page) : history.page == null;

    }

    @Override
    public int hashCode() {
        int result = manga != null ? manga.hashCode() : 0;
        result = 31 * result + (scan != null ? scan.hashCode() : 0);
        result = 31 * result + (page != null ? page.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "History{" +
                "manga=" + manga +
                ", scan=" + scan +
                ", page=" + page +
                '}';
    }
}
