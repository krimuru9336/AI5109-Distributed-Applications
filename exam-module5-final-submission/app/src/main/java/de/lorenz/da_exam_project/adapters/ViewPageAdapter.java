package de.lorenz.da_exam_project.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.lorenz.da_exam_project.fragments.ChatsFragment;
import de.lorenz.da_exam_project.fragments.UsersFragment;

public class ViewPageAdapter extends FragmentStateAdapter {

    FloatingActionButton addGroupButton;

    public ViewPageAdapter(@NonNull FragmentActivity fragmentActivity, FloatingActionButton addGroupButton) {
        super(fragmentActivity);
        this.addGroupButton = addGroupButton;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ChatsFragment();
            case 1:
                return new UsersFragment(this.addGroupButton);
            default:
                return new ChatsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
