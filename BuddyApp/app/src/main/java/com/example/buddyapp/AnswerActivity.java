package com.example.buddyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AnswerActivity extends AppCompatActivity {

String uid,que,postkey,name,url,time;
EditText editText;
Button button;
QuestionMember member;
FirebaseDatabase database = FirebaseDatabase.getInstance("https://buddyapp-5a091-default-rtdb.europe-west1.firebasedatabase.app/");
DatabaseReference allquestions;
AnswerMember member2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        member2 = new AnswerMember();
        editText = findViewById(R.id.answer_et);
        button = findViewById(R.id.btn_answer_submit);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            uid=bundle.getString("uid");
            postkey=bundle.getString("postkey");
        }else{
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
        allquestions = database.getReference("All Questions").child(postkey).child("Answer");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAnswer();
            }
        });


    }
    void saveAnswer(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuserid=  user.getUid();
        String answer = editText.getText().toString();
        if(answer!=null){
            Calendar cdate = Calendar.getInstance();
            SimpleDateFormat currentdate=new SimpleDateFormat("dd-MMMM-yyyy");
            final  String savedate=currentdate.format(cdate.getTime());
            Calendar ctime = Calendar.getInstance();
            SimpleDateFormat currenttime= new SimpleDateFormat("HH:mm:ss");
            final String savetime = currenttime.format(ctime.getTime());

          time = savedate + ":"+savetime;
          member2.setAnswer(answer);
          member2.setName(name);
          member2.setTime(time);
          member2.setUid(currentuserid);
          member2.setUrl(url);

          String id = allquestions.push().getKey();
          allquestions.child(id).setValue(member2);


            Toast.makeText(this, "Submitted", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Please write a answer", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Fetch data for the question user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuserid=  user.getUid();
        FirebaseFirestore d= FirebaseFirestore.getInstance();
        DocumentReference reference;
        reference=d.collection("user").document(currentuserid);
        reference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        runOnUiThread(() -> {
                            try {
                                url = task.getResult().getString("url");
                                name = task.getResult().getString("name");

                            } catch (Exception e) {
                                // Log the exception or show a more detailed error message
                                e.printStackTrace();
                                Toast.makeText(AnswerActivity.this, "Error updating UI for question user", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(AnswerActivity.this, "Error fetching question user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}