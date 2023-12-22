package com.example.myfirstmodule;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.ComponentActivity;

public class Android_App extends ComponentActivity {
    EditText Name, Pass;
    MyDbAdapter helper;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        Name= (EditText) findViewById(R.id.et_lusername);
        Pass= (EditText) findViewById(R.id.et_lpassword);

        helper= new MyDbAdapter(this);
    }
    public void addUser(View view)
    {
        String t1 = Name.getText().toString();
        String t2 = Pass.getText().toString();
        if(t1.isEmpty() || t2.isEmpty())
        {
            Message.message(getApplicationContext(),"Enter Both Name and Password");
        }
        else
        {
            long id = helper.insertData(t1,t2);
            if(id<=0)
            {
                Message.message(getApplicationContext(),"Insertion Unsuccessful");
                Name.setText("");
                Pass.setText("");
            } else
            {
                Message.message(getApplicationContext(),"Insertion Successful");
                Name.setText("");
                Pass.setText("");
            }
        }
    }
    public void viewdata(View view)
    {
        String data = helper.getData();
        Message.message(this,data);
    }

}
