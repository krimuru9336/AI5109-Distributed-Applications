package de.lorenz.da_exam_project.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import de.lorenz.da_exam_project.models.ChatRoom;

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
    public static void passChatRoomAsIntent(Intent intent, ChatRoom chatRoom) {
        intent.putExtra("chatRoom", chatRoom);
    }

    /**
     * Extracts the user from the given intent.
     */
    public static ChatRoom getChatRoomFromIntent(Intent intent) {
        return (ChatRoom) intent.getSerializableExtra("chatRoom");
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
    public static String getFormattedDate(Context context, long timestamp) {

        if (timestamp <= 0) return "";

        String dateFormat = "dd.MM.yyyy HH:mm";

        // set format to time if same day
        if (AndroidUtil.isSameDay(timestamp)) {
            dateFormat = "HH:mm";
        }

        Locale locale = context.getResources().getConfiguration().getLocales().get(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, locale);
        Date date = new Date(timestamp);
        return simpleDateFormat.format(date);

    }

    /**
     * Checks if the given timestamp is from the same day as today.
     */
    private static boolean isSameDay(long timestamp) {
        Date date = new Date(timestamp);
        Calendar calendarDate = Calendar.getInstance();
        calendarDate.setTime(date);

        Calendar calendarNow = Calendar.getInstance();

        return calendarDate.get(Calendar.YEAR) == calendarNow.get(Calendar.YEAR) &&
                calendarDate.get(Calendar.MONTH) == calendarNow.get(Calendar.MONTH) &&
                calendarDate.get(Calendar.DAY_OF_MONTH) == calendarNow.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Generates a random hex color, e.g. #F4D8A2.
     */
    public static String generateRandomHexColor() {
        String characters = "0123456789ABCDEF";

        StringBuilder hexColor = new StringBuilder("#");
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            char randomChar = characters.charAt(random.nextInt(characters.length()));
            hexColor.append(randomChar);
        }

        return hexColor.toString();
    }
}
