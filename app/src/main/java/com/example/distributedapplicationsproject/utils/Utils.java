package com.example.distributedapplicationsproject.utils;

import android.content.Intent;
import com.example.distributedapplicationsproject.models.User;
import com.example.distributedapplicationsproject.models.chat.Chat;
import com.example.distributedapplicationsproject.models.chat.ChatInfo;
import com.example.distributedapplicationsproject.models.chat.GroupChat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Collectors;

public class Utils {

    private static final String datePattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final String messageDatePattern = "HH:mm";

    public static String generateCreatedAt() {
        return new SimpleDateFormat(datePattern, Locale.getDefault()).format(new Date());
    }

    public static Date parseTime(String time) {
        SimpleDateFormat formatter = new SimpleDateFormat(datePattern, Locale.getDefault());
        try {
            return formatter.parse(time);
        } catch (ParseException e) {
            return null; // Return null or handle the parsing exception according to your app logic
        }
    }

    public static String parseDataToMessageTime(Date date) {
        return new SimpleDateFormat(messageDatePattern, Locale.getDefault()).format(date);
    }

    public static ChatInfo getChatInfoFromChat(Chat chat) {
        ChatInfo chatInfo = new ChatInfo();

        chatInfo.id = chat.getId();
        chatInfo.createdAt = chat.getCreatedAt();
        chatInfo.memberIdList = chat.getMemberIdList();
        chatInfo.type = chat.getType();
        switch (chat.getType()) {
            case PRIVATE:
                String otherUserId = chat.getMemberIdList().stream().filter(userId -> !userId.equals(DataShare.getInstance().getCurrentUser().getId())).collect(Collectors.toList()).get(0);
                User otherUser = DataShare.getInstance().getUserList().stream().filter(user -> user.getId().equals(otherUserId)).collect(Collectors.toList()).get(0);
                if (otherUser != null) {
                    chatInfo.title = otherUser.getName();
                } else {
                    chatInfo.title = "UNKNOWN";
                }
                break;
            case GROUP:
                chatInfo.title = ((GroupChat) chat).getTitle();
                break;
            default:
                chatInfo.title = "UNKNOWN";
                break;
        }

        return chatInfo;
    }

    public static Intent putChatInfoIntoIntent(Intent intent, Chat chat) {
        ChatInfo chatInfo = getChatInfoFromChat(chat);
        intent.putExtra("chatInfo.id", chatInfo.id);
        intent.putExtra("chatInfo.type", chatInfo.type.name());
        intent.putExtra("chatInfo.createdAt", chatInfo.createdAt);
        intent.putStringArrayListExtra("chatInfo.memberIdList", new ArrayList<>(chatInfo.memberIdList));
        intent.putExtra("chatInfo.title", chatInfo.title);

        return intent;
    }

    public static ChatInfo getChatInfoFromIntent(Intent intent) {
        ChatInfo chatInfo = new ChatInfo();

        chatInfo.id = intent.getStringExtra("chatInfo.id");
        chatInfo.type = Chat.ChatType.valueOf(intent.getStringExtra("chatInfo.type"));
        chatInfo.title = intent.getStringExtra("chatInfo.title");
        chatInfo.createdAt = intent.getStringExtra("chatInfo.createdAt");
        chatInfo.memberIdList = intent.getStringArrayListExtra("chatInfo.memberIdList");

        return chatInfo;
    }
}
