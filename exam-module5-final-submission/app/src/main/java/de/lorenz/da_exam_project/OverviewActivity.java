package de.lorenz.da_exam_project;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import de.lorenz.da_exam_project.adapters.ViewPageAdapter;
import de.lorenz.da_exam_project.models.User;
import de.lorenz.da_exam_project.utils.FirebaseUtil;

public class OverviewActivity extends AppCompatActivity {

    FloatingActionButton addGroupButton;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ViewPageAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        this.addGroupButton = findViewById(R.id.add_group_button);

        this.tabLayout = findViewById(R.id.tab_layout);
        this.viewPager2 = findViewById(R.id.view_pager);
        this.adapter = new ViewPageAdapter(this, this.addGroupButton);
        this.viewPager2.setAdapter(this.adapter);

        this.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab ignored) {
                viewPager2.setCurrentItem(tabLayout.getSelectedTabPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab ignored) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab ignored) {
            }
        });
        this.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));

                // show group add button only on users tab
                switch (position) {
                    case 0:
                        addGroupButton.hide();
                        break;
                    case 1:
                        addGroupButton.show();
                        addGroupButton.setEnabled(false);
                        break;
                }
            }
        });

        setUserInformation();
    }

    /**
     * Sets the user information text view to the current user id.
     */
    private void setUserInformation() {
        // set own user id information
        Objects.requireNonNull(FirebaseUtil.getCurrentUserDetails()).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                return;
            }

            User currentUser = task.getResult().toObject(User.class);
            String username = (currentUser != null ? currentUser.getUsername() : "Unknown");
            String userInfo = username + " (ID: " + FirebaseUtil.getCurrentUserId() + ")";
            TextView currentUserIdTextView = findViewById(R.id.current_user_id_text_view);
            currentUserIdTextView.setText(userInfo);
        });
    }
}