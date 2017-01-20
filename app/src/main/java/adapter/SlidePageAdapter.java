package adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.List;
import dto.FullPage;
import fragment.PageFragment;


public class SlidePageAdapter extends FragmentPagerAdapter {

    private List<FullPage> fullPageList;

    public SlidePageAdapter(FragmentManager fm, List<FullPage> fullPageList) {
        super(fm);
        this.fullPageList = fullPageList;
    }

    @Override
    public Fragment getItem(int position) {
        return PageFragment.create(fullPageList.get(position));
    }

    @Override
    public int getCount() {
        return fullPageList.size();
    }
}
