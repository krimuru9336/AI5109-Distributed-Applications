package demo.campuschat.adapter;

import android.view.View;

import demo.campuschat.model.Message;

public interface MessageLongClickListener {
    void onMessageLongClicked(View view, Message message, int position);
}
