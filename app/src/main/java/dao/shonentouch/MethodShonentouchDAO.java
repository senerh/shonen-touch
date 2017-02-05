package dao.shonentouch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import dto.Image;
import dto.Manga;
import dto.Page;
import dto.Scan;

public class MethodShonentouchDAO {

    public static List<Manga> getMangaList() {
        String path = "/mangas";
        return UtilsShonentouchDAO.getList(path, Manga.class);
    }

    public static List<Scan> getScanList(Manga manga) {
        String path = "/mangas/" + manga.getSlug() + "/scans";
        return UtilsShonentouchDAO.getList(path, Scan.class);
    }

    public static List<Page> getPageList(Manga manga, Scan scan) {
        String path = "/mangas/" + manga.getSlug()
                + "/scans/" + scan.getNum()
                + "/pages";
        return UtilsShonentouchDAO.getList(path, Page.class);
    }

    public static Image getImage(Manga manga, Scan scan, Page page) {
        String path = "/mangas/" + manga.getSlug() +
                "/scans/" + scan.getNum() +
                "/pages/" + page.getNum() +
                "/image";
        Image image = UtilsShonentouchDAO.get(path, Image.class);
        if (image != null) {
            Bitmap bitmap = downloadImage(image.getUrl());
            image.setImage(bitmap);
            if (bitmap == null) {
                image = null;
            }
        }
        return image;
    }

    public static Bitmap downloadImage(String address) {
        System.gc();
        Bitmap bitmap = null;
        try {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();

            byte[] bytes = ByteStreams.toByteArray(input);
            //permet de charger les jpg corrompus
            if (bytes != null && bytes.length >= 4 && bytes[0] == (byte)0xFF && bytes[1] == (byte)0xD8) {
                bytes[bytes.length - 2] = (byte) 0xFF;
                bytes[bytes.length - 1] = (byte) 0xD9;
            }

            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            connection.disconnect();
        } catch (IOException e) {
            Log.e(MethodShonentouchDAO.class.getName(), "Error while downloading image from <~" + address + "~>.");
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }
        return bitmap;
    }
}
