package com.example.whatsdown;

import static com.example.whatsdown.Constants.BASE_URL;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whatsdown.model.CreateUser;
import com.example.whatsdown.model.User;
import com.example.whatsdown.requests.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private Spinner userSpinner;
    private Button loginButton;
    private List<User> userList;
    private User selectedUser;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userSpinner = findViewById(R.id.userSpinner);
        loginButton = findViewById(R.id.loginButton);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        getUsersFromApi();
        userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get selected user
                selectedUser = userList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case where nothing is selected
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedUser != null) {
                    openChatsActivity(selectedUser);
                } else {
                    Toast.makeText(LoginActivity.this, "Select a user", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getUsersFromApi() {
        Call<List<User>> call = apiService.getUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    userList = response.body();
                    List<String> userNames = new ArrayList<>();
                    for (User user : userList) {
                        userNames.add(user.getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(LoginActivity.this,
                            android.R.layout.simple_spinner_dropdown_item, userNames);
                    userSpinner.setAdapter(adapter);
                } else {
                    System.out.println("Error: " + response.message());
                    Toast.makeText(LoginActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openChatsActivity(User selectedUser) {
        Intent intent = new Intent(LoginActivity.this, ChatsActivity.class);
        intent.putExtra("selectedUser", selectedUser);
        startActivity(intent);
    }

    public void onRegisterButtonClick(View view) {
        showNameInputDialog();
    }

    private void showNameInputDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Register");

        final EditText editTextName = new EditText(this);
        editTextName.setHint("Enter your name");
        alertDialogBuilder.setView(editTextName);

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Register", (dialog, id) -> {
                    String userName = editTextName.getText().toString();

                    if (userName.isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Enter your name", Toast.LENGTH_SHORT).show();
                    } else {
                        CreateUser user = new CreateUser(userName);
                        Call<User> call = apiService.createUser(user);
                        call.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                if (response.isSuccessful()) {
                                    User user = response.body();
                                    getUsersFromApi();
                                } else {
                                    System.out.println("Error: " + response.message());
                                    Toast.makeText(LoginActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                t.printStackTrace();
                                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}
