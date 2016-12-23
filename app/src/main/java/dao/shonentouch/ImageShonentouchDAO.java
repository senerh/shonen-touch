package dao.shonentouch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import activity.InterfaceTaskActivity;
import dto.Image;
import dto.Manga;
import dto.Page;
import dto.Scan;

public class ImageShonentouchDAO extends AbstractShonentouchDAO<Image> {

    private Manga manga;
    private Scan scan;
    private Page page;

    public ImageShonentouchDAO(InterfaceTaskActivity<Image> interfaceTaskActivity,
                               Manga manga,
                               Scan scan,
                               Page page) {
        super(interfaceTaskActivity);
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
            InputStream is = (InputStream) url.getContent();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            bitmap = BitmapFactory.decodeStream(is, null, options);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
