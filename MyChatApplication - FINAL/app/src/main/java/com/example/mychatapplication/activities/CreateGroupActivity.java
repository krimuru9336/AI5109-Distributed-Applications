package com.example.mychatapplication.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mychatapplication.databinding.ActivityCreateGroupBinding;
import com.example.mychatapplication.models.User;
import com.example.mychatapplication.utilities.Constants;
import com.example.mychatapplication.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateGroupActivity extends AppCompatActivity {

    private ActivityCreateGroupBinding binding;
    private PreferenceManager preferenceManager;
    private String encodedImage;
    List<String> selectedUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();

        Intent intent = getIntent();
        if (intent != null) {
            selectedUsers = intent.getStringArrayListExtra(Constants.KEY_SELECTED_USERS);
        }
    }

    private void setListeners() {

        binding.buttonCreate.setOnClickListener(v -> {
           if (isValidgroupDetails()){
               createGroup();
           }

        });
        binding.layoutImage.setOnClickListener(v ->{
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void createGroup() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> group = new HashMap<>();
        group.put(Constants.KEY_GROUP_NAME, binding.inputName.getText().toString());
        group.put(Constants.KEY_GROUP_IMAGE, encodedImage);

        final DocumentReference newUserRef = database.collection(Constants.KEY_COLLECTION_GROUPS).document();
        group.put(Constants.KEY_GROUP_ID,newUserRef.getId());
        newUserRef.set(group)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
                    intent.putStringArrayListExtra(Constants.KEY_SELECTED_USERS, new ArrayList<>(selectedUsers));
                    intent.putExtra("chatType", "group");
                    intent.putExtra("groupName", binding.inputName.getText().toString());
                    intent.putExtra("groupImage", encodedImage);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });
    }

    private String encodedImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes= byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK){
                    if (result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try{
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imageProfile.setImageBitmap(bitmap);
                            binding.textAddImage.setVisibility(View.GONE);
                            encodedImage = encodedImage(bitmap);
                        } catch(FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private Boolean isValidgroupDetails() {
        if (encodedImage == null) {
            showToast("Select group photo");
            return false;
        } else if (binding.inputName.getText().toString().trim().isEmpty()) {
            showToast("Enter group name");
            return false;
        } else{
            return true;
        }
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.buttonCreate.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.buttonCreate.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
