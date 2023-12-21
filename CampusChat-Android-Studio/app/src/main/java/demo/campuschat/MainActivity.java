package demo.campuschat;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import demo.campuschat.DBContract;


public class MainActivity extends AppCompatActivity {

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        dbHelper = new DBHelper(this);

        Button storeButton = findViewById(R.id.insertButton);
        Button retrieveButton = findViewById(R.id.retrieveButton);

        storeButton.setOnClickListener(view -> {
            EditText editText = (EditText) findViewById(R.id.editText);
            String inputText = (editText).getText().toString();
            insertData(inputText);
            editText.setText("");
        });

        retrieveButton.setOnClickListener(view -> {
            String data = retrieveData();
            Toast.makeText(MainActivity.this, "Hello Dear: " + data, Toast.LENGTH_SHORT).show();
        });
    }

    private void insertData(String data) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContract.DBEntry.COLUMN_NAME_DATA, data);
        db.insert(DBContract.DBEntry.TABLE_NAME, null, values);
        db.close();
        Toast.makeText(this, "Your name is successfully saved!", Toast.LENGTH_SHORT).show();
    }


    private String retrieveData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {DBContract.DBEntry.COLUMN_NAME_DATA};
        Cursor cursor = db.query(
                DBContract.DBEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        String data = null;
        if (cursor.moveToFirst()) {
            int dataIndex = cursor.getColumnIndex(DBContract.DBEntry.COLUMN_NAME_DATA);
            data = cursor.getString(dataIndex);
        }

        cursor.close();
        db.close();

        return data;
    }

    static class DBHelper extends SQLiteOpenHelper {

        static final int DATABASE_VERSION = 1;
        static final String DATABASE_NAME = "MyDB.db";

        DBHelper(MainActivity context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DBContract.SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DBContract.SQL_DELETE_ENTRIES);
            onCreate(db);
        }
    }
}
