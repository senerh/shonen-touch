package dao.shonentouch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import dto.Image;
import dto.Manga;
import dto.Page;
import dto.Scan;

public class ImageShonentouchDAO extends AbstractShonentouchDAO<Image> {

    private Manga manga;
    private Scan scan;
    private Page page;

    public ImageShonentouchDAO(InterfaceTaskShonentouchDAO<Image> interfaceTaskShonentouchDAO,
                               Manga manga,
                               Scan scan,
                               Page page) {
        super(interfaceTaskShonentouchDAO);
        this.manga = manga;
        this.scan = scan;
        this.page = page;
    }

    @Override
    protected Image doInBackground(Void... params) {
        String path = "/mangas/" + manga.getSlug() +
                "/scans/" + scan.getNum() +
                "/pages/" + page.getNum() +
                "/image";
        Image image = UtilsShonentouchDAO.get(path, Image.class);
        Bitmap bitmap = downloadImage(image.getUrl());
        image.setImage(bitmap);
        return image;
    }

    private Bitmap downloadImage(String address) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            Log.e(getClass().getName(), "Error while downloading image from <~" + address + "~>.");
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }
        return bitmap;
    }
}
