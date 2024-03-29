package com.example.whatsdownapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.whatsdownapp.model.UserModel;
import com.example.whatsdownapp.utils.AndroidUtil;
import com.example.whatsdownapp.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ProfileFragment extends Fragment {

    ImageView profilePic;
    EditText usernameInput, phoneInput;
    Button updateProfileBtn;
    ProgressBar progressBar;
    TextView logoutBtn;

    UserModel currentUser;
    ActivityResultLauncher<Intent> imagePickLauncher;

    Uri selectedImageUri;

    public ProfileFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            AndroidUtil.setProfilePic(getContext(), selectedImageUri, profilePic);
                        }
                    }

                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profilePic = view.findViewById(R.id.profile_image_view);
        usernameInput = view.findViewById(R.id.profile_username);
        phoneInput = view.findViewById(R.id.profile_phone);
        progressBar = view.findViewById(R.id.profile_progress);
        updateProfileBtn = view.findViewById(R.id.update_profile_btn);
        logoutBtn = view.findViewById(R.id.logout_btn);

        getUserData();
        updateProfileBtn.setOnClickListener(v -> {
            handleUpdateBtnClick();
        });

        logoutBtn.setOnClickListener(v -> {
            logout();
        });

        profilePic.setOnClickListener((v) -> {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
        });

        return view;
    }

    void handleUpdateBtnClick() {
        String newUsername = usernameInput.getText().toString();
        if (newUsername.isEmpty() || newUsername.length() < 3) {
            usernameInput.setError("Username length should be at least 3 chars");
            return;
        }
        currentUser.setUsername(newUsername);
        setInProgress(true);
        if (selectedImageUri != null) {
            FirebaseUtil.getCurrentProfilePicStorageRef(FirebaseUtil.currentUserId()).putFile(selectedImageUri)
                    .addOnCompleteListener(task -> {
                        updateInFireStore();
                    });
        } else {
            updateInFireStore();
        }


    }

    void updateInFireStore() {
        FirebaseUtil.currentUserDetails().set(currentUser)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        AndroidUtil.showToast(getContext(), "Profile updated Successfully");
                    } else {
                        AndroidUtil.showToast(getContext(), "Profile update Failed");
                    }
                });
    }


    void getUserData() {
        setInProgress(true);
        FirebaseUtil.getCurrentProfilePicStorageRef(FirebaseUtil.currentUserId()).getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Uri uri = task.getResult();
                        AndroidUtil.setProfilePic(getContext(), uri, profilePic);

                    }
                });
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
                currentUser = task.getResult().toObject(UserModel.class);
                usernameInput.setText(currentUser.getUsername());
                phoneInput.setText(currentUser.getPhone());
            } else {
                // Handle error
                Exception exception = task.getException();
                if (exception != null) {
                    Log.e("Firestore", "Error getting user data", exception);
                } else {
                    Log.e("Firestore", "Unknown error occurred while getting user data");
                }
            }
        });
    }

    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            updateProfileBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            updateProfileBtn.setVisibility(View.VISIBLE);
        }
    }

    void logout() {
        FirebaseUtil.logout();
        Intent intent = new Intent(getContext(), SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }


}