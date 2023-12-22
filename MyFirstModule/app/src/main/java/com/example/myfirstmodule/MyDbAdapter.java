package com.example.myfirstmodule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class MyDbAdapter {
    private MyDbHelper myhelper;

    public MyDbAdapter(Context context) {
        myhelper = new MyDbHelper(context);
    }

    public long insertData(String name, String pass) {
        try (SQLiteDatabase dbb = myhelper.getWritableDatabase()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MyDbHelper.NAME, name);
            contentValues.put(MyDbHelper.MyPASSWORD, pass);
            return dbb.insert(MyDbHelper.TABLE_NAME, null, contentValues);
        } catch (Exception e) {
            Log.e("MyDbAdapter", "Error inserting data: " + e.getMessage());
            return -1;
        }
    }

    public String getData() {
        Log.d("MyDbAdapter", "Before opening database");
        try (SQLiteDatabase db = myhelper.getWritableDatabase()) {
            Log.d("MyDbAdapter", "After opening database");
            String[] columns = {MyDbHelper.UID, MyDbHelper.NAME, MyDbHelper.MyPASSWORD};
            try (Cursor cursor = db.query(MyDbHelper.TABLE_NAME, columns, null, null, null, null, null)) {
                StringBuffer buffer = new StringBuffer();
                while (cursor.moveToNext()) {
                    int cid = cursor.getInt(cursor.getColumnIndex(MyDbHelper.UID));
                    String name = cursor.getString(cursor.getColumnIndex(MyDbHelper.NAME));
                    String password = cursor.getString(cursor.getColumnIndex(MyDbHelper.MyPASSWORD));
                    buffer.append(cid).append("   ").append(name).append("   ").append(password).append(" \n");
                }
                return buffer.toString();
            }
        } catch (Exception e) {
            Log.e("MyDbAdapter", "Error fetching data: " + e.getMessage());
            return "";
        }
    }

    static class MyDbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "myDatabase";    // Database Name
        private static final String TABLE_NAME = "myTable";   // Table Name
        private static final int DATABASE_VERSION = 1;  // Database Version
        private static final String UID = "_id";     // Column I (Primary Key)
        private static final String NAME = "Name";    // Column II
        private static final String MyPASSWORD = "Password";    // Column III
        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " VARCHAR(255), " + MyPASSWORD + " VARCHAR(255));";
        private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        private Context context;

        public MyDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE);
            } catch (Exception e) {
                Log.e("MyDbAdapter", "Error creating table: " + e.getMessage());
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                Log.i("MyDbAdapter", "OnUpgrade");
                db.execSQL(DROP_TABLE);
                onCreate(db);
            } catch (Exception e) {
                Log.e("MyDbAdapter", "Error upgrading database: " + e.getMessage());
            }
        }
    }
}
