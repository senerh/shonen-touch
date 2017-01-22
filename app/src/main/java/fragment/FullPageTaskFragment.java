package fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import java.util.List;
import dao.shonentouch.FullPageShonentouchService;
import dao.shonentouch.InterfaceFullPageShonentouchService;
import dto.FullPage;
import dto.Manga;
import dto.Scan;

public class FullPageTaskFragment extends Fragment implements InterfaceFullPageShonentouchService {

    private static final String MANGA = "fragment.FullPageTaskFragment.MANGA";
    private static final String SCAN = "fragment.FullPageTaskFragment.SCAN";

    private FullPageShonentouchService fullPageShonentouchService;
    private InterfaceFullPageShonentouchService interfaceFullPageShonentouchService;

    public static FullPageTaskFragment newInstance(Manga manga, Scan scan) {
        FullPageTaskFragment fullPageTaskFragment = new FullPageTaskFragment();

        Bundle args = new Bundle();
        args.putParcelable(MANGA, manga);
        args.putParcelable(SCAN, scan);
        fullPageTaskFragment.setArguments(args);

        return fullPageTaskFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        interfaceFullPageShonentouchService = (InterfaceFullPageShonentouchService) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Manga manga = getArguments().getParcelable(MANGA);
        Scan scan = getArguments().getParcelable(SCAN);

        fullPageShonentouchService = new FullPageShonentouchService(
                this,
                manga,
                scan);
        fullPageShonentouchService.execute();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interfaceFullPageShonentouchService = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fullPageShonentouchService.cancel(false);
    }

    @Override
    public void onPostExecute(List<FullPage> fullPageList) {
        if (interfaceFullPageShonentouchService != null) {
            interfaceFullPageShonentouchService.onPostExecute(fullPageList);
        }
    }

    @Override
    public void onProgressUpdate(FullPage fullPage) {
        if (interfaceFullPageShonentouchService != null) {
            interfaceFullPageShonentouchService.onProgressUpdate(fullPage);
        }
    }

    @Override
    public void onPreExecute() {
        if (interfaceFullPageShonentouchService != null) {
            interfaceFullPageShonentouchService.onPreExecute();
        }
    }
}
