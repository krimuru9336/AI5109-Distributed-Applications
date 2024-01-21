package com.example.chitchat;

import android.os.Looper;
import android.os.Handler;
import java.util.List;

public class UserListener {
    private final UserAdapter userAdapter;
    public UserListener(UserAdapter userAdapter){
        this.userAdapter = userAdapter;
    }
    public void onEvent(String username, String action){
        new Handler(Looper.getMainLooper()).post(()->{
                        switch (action){
                            case("connect"):
                                userAdapter.addUser(username);
                                break;
                            case ("disconnect"):
                                MessageStore.clearMessages(username);
                                userAdapter.removeUser(username);
                                break;
                        }
                }
        );
    }
    public void onEvent(List<String> usernameList, String action){
        new Handler(Looper.getMainLooper()).post(()-> {
            if(userAdapter != null){
                for(String username : usernameList){
                    userAdapter.addUser(username);
                }
            }
        });
    }
}
