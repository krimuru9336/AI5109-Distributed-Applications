package com.example.buddyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.C;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AskActivity extends AppCompatActivity {

    EditText editText;
    Button button;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://buddyapp-5a091-default-rtdb.europe-west1.firebasedatabase.app");
    DatabaseReference AllQuestions,UserQuestions;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    QuestionMember member;
    String name,url,privacy,uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuserid = user.getUid();

        editText = findViewById(R.id.ask_et_questions);
        button=findViewById(R.id.btn_submit);
        documentReference =db.collection("user").document(currentuserid);

        AllQuestions = database.getReference("All Questions");
        UserQuestions = database.getReference("Users Questions").child(currentuserid);

        member =new QuestionMember();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String questions = editText.getText().toString();

                Calendar cdate = Calendar.getInstance();
                SimpleDateFormat currentdate=new SimpleDateFormat("dd-MMMM-yyyy");
                final  String savedate=currentdate.format(cdate.getTime());
                Calendar ctime = Calendar.getInstance();
                SimpleDateFormat currenttime= new SimpleDateFormat("HH:mm:ss");
                final String savetime = currenttime.format(ctime.getTime());

                String time = savedate + ":"+savetime;

                if(questions != null){
                    member.setQuestion(questions);
                    member.setName(name);
                    member.setPrivacy(privacy);
                    member.setUrl(url);
                    member.setUserid(uid);
                    member.setTime(time);

                    String id= UserQuestions.push().getKey();
                    UserQuestions.child(id).setValue(member);

                    String child= AllQuestions.push().getKey();
                    member.setKey(id);
                    AllQuestions.child(child).setValue(member);

                    Log.d( "onClick: ", child);
                    Log.d("link aaaa",AllQuestions.toString());
                    Toast.makeText(AskActivity.this, AllQuestions.toString(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(AskActivity.this, "Submitted "+child, Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(AskActivity.this, "Please ask a Questions", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()) {
                    name= task.getResult().getString("name");
                    url = task.getResult().getString("url");
                    privacy = task.getResult().getString("privacy");
                    uid=task.getResult().getString("uid");
                    Log.d("uid on 110",name);
                }else {
                    Toast.makeText(AskActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}