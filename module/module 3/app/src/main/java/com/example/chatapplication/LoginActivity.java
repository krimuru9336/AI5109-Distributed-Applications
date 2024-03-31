package com.example.chatapplication;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.EXTRA_CREATED_AT;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.EXTRA_EMAIL;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.EXTRA_PASSWORD;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.EXTRA_USERNAME;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.REF_USERS;
import static com.bytesbee.firebase.chat.activities.constants.IConstants.TYPE_EMAIL;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.bytesbee.firebase.chat.activities.managers.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import com.bytesbee.firebase.chat.activities.managers.Utils;
import com.bytesbee.firebase.chat.activities.views.SingleClickListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Objects;

public class LoginActivity extends BaseActivity {

    private EditText mTxtEmail;
    private EditText mTxtPassword;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mTxtEmail = findViewById(R.id.txtEmail);
        mTxtPassword = findViewById(R.id.txtPassword);
        final Button mBtnSignUp = findViewById(R.id.btnSignUp);
        final GoogleSignInButton btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        final TextView mTxtNewUser = findViewById(R.id.txtNewUser);
        final TextView txtForgetPassword = findViewById(R.id.txtForgetPassword);

        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        } catch (Exception e) {
            Utils.getErrors(e);
        }

        auth = FirebaseAuth.getInstance();

        Utils.setHTMLMessage(mTxtNewUser, getString(R.string.strNewSignUp));

        txtForgetPassword.setOnClickListener(new SingleClickListener() {
            @Override
            public void onClickView(View v) {
                screens.showCustomScreen(ForgetPasswordActivity.class);
            }
        });

        mBtnSignUp.setOnClickListener(new SingleClickListener() {
            @Override
            public void onClickView(View v) {
                String strEmail = mTxtEmail.getText().toString().trim();
                String strPassword = mTxtPassword.getText().toString().trim();

                if (TextUtils.isEmpty(strEmail) || TextUtils.isEmpty(strPassword)) {
                    screens.showToast(R.string.strAllFieldsRequired);
                } else {
                    login(strEmail, strPassword);
                }
            }
        });
        mTxtNewUser.setOnClickListener(new SingleClickListener() {
            @Override
            public void onClickView(View v) {
                screens.showCustomScreen(RegisterActivity.class);
            }
        });
        btnGoogleSignIn.setOnClickListener(new SingleClickListener() {
            @Override
            public void onClickView(View v) {
                signIn();
            }
        });

    }

    private void login(String email, String password) {
        showProgress();

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            hideProgress();
            if (task.isSuccessful()) {
                screens.showClearTopScreen(MainActivity.class);
            } else {
                screens.showToast(R.string.strInvalidEmailPassword);
            }
        }).addOnFailureListener(e -> hideProgress()).addOnCanceledListener(this::hideProgress);
    }

    private void signIn() {
        try {
            showProgress();
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        try {
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            auth.signInWithCredential(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    Utils.sout("User::: " + user.getPhotoUrl());
                    final String userId = Objects.requireNonNull(user).getUid();
                    try {
                        final String username = Utils.isEmpty(user.getDisplayName()) ? user.getEmail() : user.getDisplayName();
                        reference = FirebaseDatabase.getInstance().getReference(REF_USERS).child(userId);
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                try {
                                    Utils.sout("UserID Available::: " + userId + " >> " + dataSnapshot.hasChildren());
                                    if (dataSnapshot.hasChildren()) {
                                        hideProgress();
                                        screens.showClearTopScreen(MainActivity.class);
                                    } else {
                                        final HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put(EXTRA_ID, userId);
                                        hashMap.put(EXTRA_EMAIL, user.getEmail());
                                        hashMap.put(EXTRA_USERNAME, Utils.getCapsWord(username));
                                        hashMap.put(EXTRA_PASSWORD, user.getEmail());
                                        hashMap.put(EXTRA_IMAGEURL, Utils.isEmpty(user.getPhotoUrl()) ? IMG_DEFAULTS : URLEncoder.encode(user.getPhotoUrl().toString(), "UTF-8"));
                                        hashMap.put(EXTRA_ACTIVE, true);
                                        hashMap.put(EXTRA_IS_ONLINE, STATUS_ONLINE);
                                        hashMap.put(EXTRA_SEARCH, Objects.requireNonNull(username).toLowerCase().trim());
                                        hashMap.put(EXTRA_CREATED_AT, Utils.getDateTime());
                                        hashMap.put(EXTRA_VERSION, BuildConfig.VERSION_NAME);
                                        hashMap.put(EXTRA_SIGNUP_TYPE, TYPE_GOOGLE);
                                        hashMap.put(EXTRA_SOCIAL_TOKEN, idToken);

                                        reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                                            try {
                                                if (task1.isSuccessful()) {
                                                    hideProgress();
                                                    screens.showClearTopScreen(MainActivity.class);
                                                }
                                            } catch (Exception e) {
                                                Utils.getErrors(e);
                                            }
                                        });
                                    }
                                } catch (Exception ignored) {
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                hideProgress();
                            }
                        });
                    } catch (Exception e) {
                        hideProgress();
                        Utils.getErrors(e);
                    }
                } else {
                    hideProgress();
                    Utils.getErrors(task.getException());
                    screens.showToast(Objects.requireNonNull(task.getException()).getLocalizedMessage());
                }
            }).addOnFailureListener(e -> {
                hideProgress();
                screens.showToast(e.getMessage());
            }).addOnCanceledListener(this::hideProgress);
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            try {
                final Intent data = result.getData();
                assert data != null;
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {/
                    Utils.getErrors(e);
                }
            } catch (Exception e) {
                Utils.getErrors(e);
            }
        }
    });
}