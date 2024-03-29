package com.example.mysheetchatda.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.mysheetchatda.Fragment.ChatFragment;

/*
- Name: Adrianus Jonathan Engelbracht
- Matriculation number: 1151826
- Date: 02.02.2024
*/
public class FragmentAdapter extends FragmentPagerAdapter {


    public FragmentAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: new ChatFragment();
                //case 1: new TestFragment();
            default: return new ChatFragment();
        }
    }

    @Override
    public int getCount() {

        return 1;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        String title = null;
        if(position == 0){
            title = "CHATS";
        }
        /*
        if(position == 1){
            title = "TEST";
        }*/
        return title;
    }
}
