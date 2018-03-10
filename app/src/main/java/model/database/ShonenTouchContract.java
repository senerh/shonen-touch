package model.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Thibaut SORIANO on 29/03/2017.
 */

public final class ShonenTouchContract {
    public static final String AUTHORITY = "io.github.senerh.shonentouch";

    /**
     * This class can't be instantiated
     */
    private ShonenTouchContract() {

    }

    public interface MangaColumns extends BaseColumns {

        // Define the table schema
        public static final String NAME = "name";
        public static final String SLUG = "slug";

        String[] PROJECTION = {
                _ID,
                NAME,
                SLUG
        };
    }

    public static final class Manga {

        // This class cannot be instantiated.
        private Manga() { }

        /**
         * The table that stores mangas.
         */
        public static final String TABLE_NAME = "manga";
        /**
         * The {@link Uri} for querying manga.
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
        /**
         * The {@link Uri} for querying a particular manga.
         */
        public static final Uri CONTENT_ID_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME + "/");
        /**
         * The MIME type for multiple mangas.
         */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "." + TABLE_NAME;
        /**
         * The MIME type for a particular manga.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "." + TABLE_NAME;
    }

    public interface ScanColumns extends BaseColumns {

        // Define the table schema
        public static final String NAME = "name";
        public static final String DOWNLOAD_TIMESTAMP = "downloadTimestamp";
        public static final String LAST_READ_PAGE = "lastReadPage";
        public static final String STATUS = "status";
        public static final String DOWNLOAD_STATUS = "downloadStatus";
        public static final String MANGA_ID = "mangaId";

        String[] PROJECTION = {
                _ID,
                NAME,
                DOWNLOAD_TIMESTAMP,
                LAST_READ_PAGE,
                STATUS,
                DOWNLOAD_STATUS,
                MANGA_ID
        };
    }

    public static final class Scan {

        // This class cannot be instantiated.
        private Scan() { }

        /**
         * The table that stores mangas.
         */
        public static final String TABLE_NAME = "scan";
        /**
         * The {@link Uri} for querying manga.
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
        /**
         * The {@link Uri} for querying a particular manga.
         */
        public static final Uri CONTENT_ID_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME + "/");
        /**
         * The MIME type for multiple mangas.
         */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "." + TABLE_NAME;
        /**
         * The MIME type for a particular manga.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "." + TABLE_NAME;
    }

    public interface PageColumns extends BaseColumns {

        // Define the table schema
        public static final String PATH = "path";
        public static final String SCAN_ID = "scanId";

        String[] PROJECTION = {
                _ID,
                PATH,
                SCAN_ID
        };
    }

    public static final class Page {

        // This class cannot be instantiated.
        private Page() { }

        /**
         * The table that stores mangas.
         */
        public static final String TABLE_NAME = "page";
        /**
         * The {@link Uri} for querying manga.
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
        /**
         * The {@link Uri} for querying a particular manga.
         */
        public static final Uri CONTENT_ID_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME + "/");
        /**
         * The MIME type for multiple mangas.
         */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "." + TABLE_NAME;
        /**
         * The MIME type for a particular manga.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "." + TABLE_NAME;
    }
}
