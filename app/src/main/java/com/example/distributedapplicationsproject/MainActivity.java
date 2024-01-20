package com.example.distributedapplicationsproject;

import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //private DatabaseHelper databaseHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //databaseHelper = new DatabaseHelper(this.getApplicationContext());

        EditText textName = findViewById(R.id.inputTextName);

        textName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    return !validateName(textName);
                }
                return false;
            }
        });

    }

    private boolean validateName(EditText editText) {
        String input = editText.getText().toString().trim();

        boolean validated = input.matches("^\\w+$");
        if (validated) {
            editText.setError(null);
        } else {
            editText.setError("Invalid name");
        }

        return validated;
    }

    public void onClickInsert(View view) {
        EditText textName = findViewById(R.id.inputTextName);
        if (validateName(textName)) {
//            if (databaseHelper.addName(String.valueOf(textName.getText())) == -1) {
//                Toast.makeText(getApplicationContext(), "Failed to insert\n" + textName.getText(), Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(getApplicationContext(), "Inserted Name\n" + textName.getText(), Toast.LENGTH_LONG).show();
//            }
        }
    }

    public void onClickRetrieve(View view) {
//        Cursor cursor = databaseHelper.getAllData();
//        if (cursor.getCount() <= 0) {
//            Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
//        } else {
//            if (cursor.moveToLast()) {
//                Toast.makeText(getApplicationContext(), cursor.getInt(0) + ": " + cursor.getString(1), Toast.LENGTH_LONG).show();
//            }
//        }
    }

}
