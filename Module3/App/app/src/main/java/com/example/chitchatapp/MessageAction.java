/*  Christian Jumtow
    Matr. Nr: 1166358
 */

package com.example.chitchatapp;

import androidx.annotation.NonNull;

public enum MessageAction {
    MESSAGE,
    EDIT,
    DELETE;

    @NonNull
    @Override
    public String toString() {
        switch (this) {
            case MESSAGE:
                return "message";
            case EDIT:
                return "edit";
            case DELETE:
                return "delete";
            default:
                return "";
        }
    }
}