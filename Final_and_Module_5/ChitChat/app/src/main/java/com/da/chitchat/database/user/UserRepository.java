package com.da.chitchat.database.user;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.da.chitchat.models.User;

import java.util.UUID;

public class UserRepository {
    private final UserDbHelper dbHelper;

    public UserRepository(Context context) {
        dbHelper = new UserDbHelper(context);
    }

    public void saveUser(UUID id, String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //db.delete(UserContract.UserEntry.TABLE_NAME, null, null);

        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.COLUMN_NAME_ID, id.toString());
        values.put(UserContract.UserEntry.COLUMN_NAME_USERNAME, name);
        db.insertWithOnConflict(UserContract.UserEntry.TABLE_NAME, null, values,
                SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public User getUser() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                UserContract.UserEntry.TABLE_NAME,
                new String[]{UserContract.UserEntry.COLUMN_NAME_ID, UserContract.UserEntry.COLUMN_NAME_USERNAME},
                null,
                null,
                null,
                null,
                null
        );

        User user = null;

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_NAME_ID);
            int usernameIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_NAME_USERNAME);

            if (idIndex >= 0 && usernameIndex >= 0) {
                String id = cursor.getString(idIndex);
                String username = cursor.getString(usernameIndex);
                user = new User(id, username);
            }
        }

        cursor.close();
        db.close();

        return user;
    }
}
