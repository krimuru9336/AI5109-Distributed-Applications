package de.lorenz.da_exam_project.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.lorenz.da_exam_project.models.User;

public class AndroidUtil {

    /**
     * Shows a toast with the given message.
     */
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Passes the given user as an extra to the given intent.
     */
    public static void passUserAsIntentExtra(Intent intent, User user) {
        intent.putExtra("user", user);
    }

    /**
     * Extracts the user from the given intent.
     */
    public static User getUserFromIntent(Intent intent) {
        return (User) intent.getSerializableExtra("user");
    }

    /**
     * Builds a chat room id based on the two given user ids.
     */
    public static String getChatRoomId(String userId1, String userId2) {
        return userId1.compareTo(userId2) < 0 ? userId1 + "_" + userId2 : userId2 + "_" + userId1;
    }

    /**
     * Returns a formatted date string for the given timestamp.
     */
    public static String getFormattedDate(Context context, Timestamp timestamp) {
        String dateFormat = "dd.MM.yyyy HH:mm";

        // set format to time if same day
        if (AndroidUtil.isSameDay(timestamp)) {
            dateFormat = "HH:mm";
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, context.getResources().getConfiguration().locale);
        Date date = timestamp.toDate();
        return simpleDateFormat.format(date);
    }

    /**
     * Checks if the given timestamp is from the same day as today.
     */
    private static boolean isSameDay(Timestamp timestamp) {
        Date date = timestamp.toDate();
        Date now = new Date();
        return date.getYear() == now.getYear() && date.getMonth() == now.getMonth() && date.getDay() == now.getDay();
    }
}
