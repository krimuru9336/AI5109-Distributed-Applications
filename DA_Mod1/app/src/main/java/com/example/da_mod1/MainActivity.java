package com.example.da_mod1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView name = (TextView)findViewById(R.id.editTextText);
        Button btnSave = (Button)findViewById(R.id.buttonSave);

        TextView id = (TextView)findViewById(R.id.editTextID);
        Button btnGet = (Button)findViewById(R.id.buttonGet);

        TextView nameId = (TextView)findViewById(R.id.textViewNameID);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Connection connection = ConnectionHelper.connectionClass();

                try {
                    if(connection != null){
                        String sqlInsert = "Insert into DA_Mod1 values('"+name.getText().toString()+"')";
                        Statement st = connection.createStatement();
                        ResultSet rs = st.executeQuery(sqlInsert);
                    }
                }
                catch (Exception e){
                    Log.e("Error", e.getMessage());
                }

            }

        });

        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Connection connection = ConnectionHelper.connectionClass();

                try {
                    if(connection != null){
                        String sqlInsert = "Select * from DA_Mod1 WHERE ID = '"+id.getText().toString()+"'";
                        Statement st = connection.createStatement();
                        ResultSet rs = st.executeQuery(sqlInsert);

                        while (rs.next()){

                            Toast.makeText(MainActivity.this, "Retrieved from DB: ID:"+rs.getString(1)+
                                    "\nName:"+rs.getString(2)+"  ", Toast.LENGTH_LONG).show();
                            nameId.setText(rs.getString(2));
                        }
                    }
                }
                catch (Exception e){
                    Log.e("Error", e.getMessage());
                }

            }

        });

    }
}