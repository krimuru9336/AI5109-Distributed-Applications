package Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import Adapters.TabAdapter;



import com.example.chitchat.R;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;



public class OverlayMenuFragment extends DialogFragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    public OverlayMenuFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_layout, container, false);
    }
    private void setupViewPager(ViewPager viewPager) {
        TabAdapter adapter = new TabAdapter(getChildFragmentManager());
        adapter.addFragment(new UserTabFragment(), "Find Friend");
        adapter.addFragment(new GroupTabFragment(), "Create Group");
        viewPager.setAdapter(adapter);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        setupViewPager(viewPager);
    }

}
