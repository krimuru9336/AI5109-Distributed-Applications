package com.example.distributedapplicationsproject.models.chat;

import com.example.distributedapplicationsproject.utils.Utils;

import java.util.UUID;

public class PrivateChat extends Chat {
    public PrivateChat() {
        super();
        this.type = ChatType.PRIVATE;
    }
}
