package dto;

import android.os.Parcel;
import android.os.Parcelable;

public class Manga implements Parcelable {

    private String slug;
    private String name;

    public Manga() {

    }

    public Manga(String slug, String name) {
        this.slug = slug;
        this.name = name;
    }

    private Manga(Parcel in) {
        slug = in.readString();
        name = in.readString();
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Manga)) return false;

        Manga manga = (Manga) o;

        if (slug != null ? !slug.equals(manga.slug) : manga.slug != null) return false;
        return name != null ? name.equals(manga.name) : manga.name == null;
    }

    @Override
    public int hashCode() {
        int result = slug != null ? slug.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Manga{" +
                "slug='" + slug + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(slug);
        dest.writeString(name);
    }

    public static final Parcelable.Creator<Manga> CREATOR = new Parcelable.Creator<Manga>() {
        @Override
        public Manga createFromParcel(Parcel source) {
            return new Manga(source);
        }

        @Override
        public Manga[] newArray(int size) {
            return new Manga[size];
        }
    };
}
