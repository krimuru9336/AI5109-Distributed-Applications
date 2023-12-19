package com.da.chitchat;

import android.provider.BaseColumns;

public class NameContract {
    private NameContract() {}

    public static class NameEntry implements BaseColumns {
        public static final String TABLE_NAME = "name_entry";
        public static final String COLUMN_NAME = "name";
    }
}
