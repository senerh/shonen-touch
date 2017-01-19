package dto;

import android.os.Parcel;
import android.os.Parcelable;

public class FullPage implements Parcelable {

    private Page page;
    private Image image;

    public FullPage() {

    }

    public FullPage(Page page, Image image) {
        this.image = image;
        this.page = page;
    }

    private FullPage(Parcel in) {
        page = in.readParcelable(Page.class.getClassLoader());
        image = in.readParcelable(Image.class.getClassLoader());
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FullPage fullPage = (FullPage) o;

        if (page != null ? !page.equals(fullPage.page) : fullPage.page != null) return false;
        return image != null ? image.equals(fullPage.image) : fullPage.image == null;

    }

    @Override
    public int hashCode() {
        int result = page != null ? page.hashCode() : 0;
        result = 31 * result + (image != null ? image.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FullPage{" +
                "page=" + page +
                ", image=" + image +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(page);
        dest.writeValue(image);
    }

    public static final Creator<FullPage> CREATOR = new Creator<FullPage>() {
        @Override
        public FullPage createFromParcel(Parcel source) {
            return new FullPage(source);
        }

        @Override
        public FullPage[] newArray(int size) {
            return new FullPage[size];
        }
    };
}
