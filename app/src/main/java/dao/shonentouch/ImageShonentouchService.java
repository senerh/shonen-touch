package dao.shonentouch;

import dto.Image;
import dto.Manga;
import dto.Page;
import dto.Scan;

public class ImageShonentouchService extends AbstractShonentouchDAO<Image> {

    private Manga manga;
    private Scan scan;
    private Page page;

    public ImageShonentouchService(InterfaceTaskShonentouchService<Image> interfaceTaskShonentouchService,
                                   Manga manga,
                                   Scan scan,
                                   Page page) {
        super(interfaceTaskShonentouchService);
        this.manga = manga;
        this.scan = scan;
        this.page = page;
    }

    @Override
    protected Image doInBackground(Void... params) {
        return MethodShonentouchDAO.getImage(manga, scan, page);
    }
}
