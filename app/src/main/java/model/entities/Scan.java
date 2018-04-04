package model.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Johnhetour on 08/07/2017.
 */

public class Scan implements Parcelable {
    public static final Creator<Scan> CREATOR = new Creator<Scan>() {
        @Override
        public Scan createFromParcel(Parcel in) {
            return new Scan(in);
        }

        @Override
        public Scan[] newArray(int size) {
            return new Scan[size];
        }
    };

    private String mName;

    private long mDownloadTimestamp;

    private Status mStatus;

    private String mDownloadStatus;

    private int mLastReadPage;

    public enum Status {
        NOT_DOWNLOADED,
        DOWNLOAD_IN_PROGRESS,
        DOWNLOAD_STOPPED,
        DOWNLOAD_COMPLETE
    }

    public Scan() {
    }

    public Scan(String name) {
        mName = name;
    }

    protected Scan(Parcel in) {
        mName = in.readString();
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
        dest.writeString(mName);
    }

    public long getDownloadTimestamp() {
        return mDownloadTimestamp;
    }

    public void setDownloadTimestamp(long downloadTimestamp) {
        this.mDownloadTimestamp = downloadTimestamp;
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        this.mStatus = status;
    }

    public int getLastReadPage() {
        return mLastReadPage;
    }

    public void setLastReadPage(int lastReadPage) {
        this.mLastReadPage = lastReadPage;
    }

    public String getDownloadStatus() {
        return mDownloadStatus;
    }

    public void setDownloadStatus(String downloadStatus) {
        this.mDownloadStatus = downloadStatus;
    }
}
