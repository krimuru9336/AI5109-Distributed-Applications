package com.example.whatsdown;

import static com.example.whatsdown.Constants.BASE_URL;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.whatsdown.model.User;
import com.example.whatsdown.requests.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatsActivity extends AppCompatActivity {
    private LinearLayout userListContainer;
    private List<User> userList;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        userListContainer = findViewById(R.id.userListContainer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setTitle("Chats");
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        User selectedUser = (User) getIntent().getSerializableExtra("selectedUser");
        getUsersFromApi(selectedUser);
    }

    private void addUserItem(final User user, final User selectedUser) {
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 8, 0, 8);
        textView.setLayoutParams(layoutParams);
        textView.setText(user.getName());
        textView.setTextSize(18);
        textView.setPadding(16, 16, 16, 16);
        textView.setBackgroundResource(R.drawable.user_item_background);

        textView.setOnClickListener(v -> onItemClick(user, selectedUser));

        userListContainer.addView(textView);
    }

    private void getUsersFromApi(User loggedInUser) {
        Call<List<User>> call = apiService.getUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    userList = response.body();
                    for (User user : userList) {
                        if (user.getUserId() != loggedInUser.getUserId()) {
                            addUserItem(user, loggedInUser);
                        }
                    }
                } else {
                    System.out.println("Error: " + response.message());
                    Toast.makeText(ChatsActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(ChatsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onItemClick(User user, User loggedInUser) {
        Intent intent = new Intent(ChatsActivity.this, MainActivity.class);
        intent.putExtra("selectedUser", user);
        intent.putExtra("loggedInUser", loggedInUser);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        OnBackPressedDispatcher dispatcher = getOnBackPressedDispatcher();
        dispatcher.onBackPressed();
        return true;
    }

}
