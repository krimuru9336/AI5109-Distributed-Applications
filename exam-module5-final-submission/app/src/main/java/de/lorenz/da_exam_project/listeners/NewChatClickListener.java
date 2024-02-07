package de.lorenz.da_exam_project.listeners;

import android.content.Context;
import android.view.View;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import de.lorenz.da_exam_project.models.ChatRoom;
import de.lorenz.da_exam_project.models.User;
import de.lorenz.da_exam_project.utils.AndroidUtil;
import de.lorenz.da_exam_project.utils.ChatRoomUtil;
import de.lorenz.da_exam_project.utils.FirebaseUtil;

public class NewChatClickListener implements View.OnClickListener {

    Context context;
    ChatRoom model;
    User partner;

    public NewChatClickListener(Context context, ChatRoom model, User partner) {
        super();
        this.context = context;
        this.model = model;
        this.partner = partner;
    }

    @Override
    public void onClick(View v) {

        // if chat room doesn't exist, create it
        if (this.model == null) {
            String chatRoomId = AndroidUtil.getChatRoomId(Objects.requireNonNull(FirebaseUtil.getCurrentUserId()), this.partner.getUserId());
            List<String> userIds = Arrays.asList(FirebaseUtil.getCurrentUserId(), this.partner.getUserId());
            this.model = new ChatRoom(chatRoomId, userIds, 0, "", "", "", "");

            // add chat room model to database
            FirebaseUtil.getChatRoomReference(this.model.getId()).set(this.model).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    AndroidUtil.showToast(this.context, "Unable to create chat room!");
                    return;
                }

                ChatRoomUtil.openChatRoom(this.context, this.model);
            });
            return;
        }

        ChatRoomUtil.openChatRoom(this.context, this.model);
    }
}
