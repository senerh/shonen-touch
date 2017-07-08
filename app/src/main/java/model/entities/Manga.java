package model.entities;

/**
 * Created by Johnhetour on 08/07/2017.
 */

public class Manga {
    private String mSlug;

    private String mName;

    public Manga() {
    }

    public Manga(String name, String slug) {
        mSlug = slug;
        mName = name;
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
}
