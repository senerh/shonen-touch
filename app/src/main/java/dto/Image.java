package dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public class Image implements Parcelable {

    private String url;

    @JsonIgnore
    private byte[] imageBytes;

    public Image() {

    }

    public Image(String url) {
        this.url = url;
    }

    private Image(Parcel in) {
        url = in.readString();
        in.readByteArray(imageBytes);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @JsonIgnore
    public byte[] getImageBytes() {
        return imageBytes;
    }

    @JsonProperty
    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Image image = (Image) o;

        if (url != null ? !url.equals(image.url) : image.url != null) return false;
        return Arrays.equals(imageBytes, image.imageBytes);
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(imageBytes);
        return result;
    }

    @Override
    public String toString() {
        return "Image{" +
                "url='" + url + '\'' +
                ", imageBytes=" + Arrays.toString(imageBytes) +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeByteArray(imageBytes);
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}
