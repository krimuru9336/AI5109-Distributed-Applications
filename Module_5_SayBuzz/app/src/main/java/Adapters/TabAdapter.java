package Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import java.util.ArrayList;
import java.util.List;

public class TabAdapter extends FragmentPagerAdapter {

    private final List<Fragment> myFragmentList = new ArrayList<>();
  
    @Override
    public CharSequence getPageTitle(int position) {
        return myTitlefragmentList.get(position);
    }

    private final List<String> myTitlefragmentList = new ArrayList<>();

    @Override
    public int getCount() {
        return myFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        myFragmentList.add(fragment);
        myTitlefragmentList.add(title);
    }


    public TabAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return myFragmentList.get(position);
    }

    


}
