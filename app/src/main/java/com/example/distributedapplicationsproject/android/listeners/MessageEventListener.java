package com.example.distributedapplicationsproject.android.listeners;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.distributedapplicationsproject.android.MessageRecyclerAdapter;
import com.example.distributedapplicationsproject.models.Message;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class MessageEventListener implements ChildEventListener {

    List<Message> messageList;
    MessageRecyclerAdapter messageRecyclerAdapter;

    public MessageEventListener(List<Message> messageList, MessageRecyclerAdapter messageRecyclerAdapter) {
        this.messageList = messageList;
        this.messageRecyclerAdapter = messageRecyclerAdapter;
    }

    @Override
    public void onChildAdded(@NonNull @NotNull DataSnapshot dataSnapshot, @Nullable @org.jetbrains.annotations.Nullable String s) {
        Message msg = dataSnapshot.getValue(Message.class);
        if (msg != null) {
            messageList.add(msg);
            messageRecyclerAdapter.notifyItemInserted(messageList.size() - 1);
        }
    }

    @Override
    public void onChildChanged(@NonNull @NotNull DataSnapshot dataSnapshot, @Nullable @org.jetbrains.annotations.Nullable String s) {
        Message msg = dataSnapshot.getValue(Message.class);
        int index = Integer.parseInt(Objects.requireNonNull(dataSnapshot.getKey()));
        if (msg != null & index >= 0 & index < messageList.size()) {
            messageList.set(index, msg);
            messageRecyclerAdapter.notifyItemChanged(index, msg);
        }
    }

    @Override
    public void onChildRemoved(@NonNull @NotNull DataSnapshot dataSnapshot) {
        Message msg = dataSnapshot.getValue(Message.class);
        int index = Integer.parseInt(Objects.requireNonNull(dataSnapshot.getKey()));
        if (msg != null & index >= 0 & index < messageList.size()) {
            messageList.remove(index);
            messageRecyclerAdapter.notifyItemRemoved(index);
        }
    }

    @Override
    public void onChildMoved(@NonNull @NotNull DataSnapshot dataSnapshot, @Nullable @org.jetbrains.annotations.Nullable String s) {
        // Not implemented
    }

    @Override
    public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
        // Not implemented
    }
}
