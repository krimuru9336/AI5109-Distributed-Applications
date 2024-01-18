package de.lorenz.da_exam_project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.lorenz.da_exam_project.R;
import de.lorenz.da_exam_project.models.ChatMessage;
import de.lorenz.da_exam_project.utils.AndroidUtil;
import de.lorenz.da_exam_project.utils.FirebaseUtil;

public class ChatRoomRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessage, ChatRoomRecyclerAdapter.ChatRoomModelViewHolder> {

    Context context;

    public ChatRoomRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessage> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatRoomModelViewHolder holder, int position, @NonNull ChatMessage model) {
        System.out.println("ChatRoomRecyclerAdapter.onBindViewHolder");
        System.out.println("model = " + model.getMessage());

        // decide whether to show the message on the left or right side based on the sender id
        if (model.getSenderId().equals(FirebaseUtil.getCurrentUserId())) {
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatTextView.setText(model.getMessage());
            holder.rightChatTimestampTextView.setText(AndroidUtil.getFormattedDate(context, model.getTimestamp()));
        } else {
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatTextView.setText(model.getMessage());
            holder.leftChatTimestampTextView.setText(AndroidUtil.getFormattedDate(context, model.getTimestamp()));
        }
    }

    @NonNull
    @Override
    public ChatRoomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_recycler_view, parent, false);
        return new ChatRoomModelViewHolder(view);
    }

    static class ChatRoomModelViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftChatLayout;
        LinearLayout rightChatLayout;
        TextView leftChatTextView;
        TextView rightChatTextView;
        TextView leftChatTimestampTextView;
        TextView rightChatTimestampTextView;

        public ChatRoomModelViewHolder(View itemView) {
            super(itemView);

            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextView = itemView.findViewById(R.id.left_chat_text_view);
            rightChatTextView = itemView.findViewById(R.id.right_chat_text_view);
            leftChatTimestampTextView = itemView.findViewById(R.id.left_chat_timestamp);
            rightChatTimestampTextView = itemView.findViewById(R.id.right_chat_timestamp);
        }
    }
}
