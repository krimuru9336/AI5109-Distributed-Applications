// Sven Schickentanz - fdai7287
package com.da.chitchat.database.user;

import android.provider.BaseColumns;

/**
 * The contract class for the user table in the database.
 * This class defines the table name and column names for the users table.
 */
public class UserContract {
    private UserContract() {}

    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "user_entry";
        public static final String COLUMN_NAME_ID = "uuid";
        public static final String COLUMN_NAME_USERNAME = "username";
    }
}
