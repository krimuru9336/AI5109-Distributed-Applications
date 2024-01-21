package com.example.chitchat

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.example.chitchat.models.ChatroomModel
import com.example.chitchat.adapter.ChatRecyclerAdapter
import com.example.chitchat.models.ChatmessageModel
import com.example.chitchat.models.UserModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import java.util.Arrays


class ChatActivity : AppCompatActivity() {
    var otherUser: UserModel? = null
    var chatroomId: String? = null
    var chatroomModel: ChatroomModel? = null
    var adapter: ChatRecyclerAdapter? = null
    lateinit var messageInput: EditText
    lateinit var sendMessageBtn: ImageButton
    lateinit var backBtn: ImageButton
    lateinit var otherUsername: TextView
    lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        //get UserModel
        otherUser = Utils.getUserModelFromIntent(intent)
        chatroomId = otherUser!!.userId?.let { Utils.getChatroomId(Utils.currentUserId()!!, it) }
        messageInput = findViewById(R.id.chat_message_input)
        sendMessageBtn = findViewById(R.id.message_send_btn)
        backBtn = findViewById(R.id.back_btn)
        otherUsername = findViewById(R.id.other_username)
        recyclerView = findViewById(R.id.chat_recycler_view)
        backBtn.setOnClickListener(View.OnClickListener { v: View? -> onBackPressed() })
        otherUsername.setText(otherUser!!.username)
        sendMessageBtn.setOnClickListener(View.OnClickListener { v: View? ->
            val message = messageInput.getText().toString().trim { it <= ' ' }
            if (message.isEmpty()) return@OnClickListener
            sendMessageToUser(message)
        })
        orCreateChatroomModel
        setupChatRecyclerView()
    }

    fun setupChatRecyclerView() {
        val query = Utils.getChatroomMessageReference(chatroomId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
        val options: FirestoreRecyclerOptions<ChatmessageModel> =
            FirestoreRecyclerOptions.Builder<ChatmessageModel>()
                .setQuery(query, ChatmessageModel::class.java).build()
        adapter = ChatRecyclerAdapter(options, applicationContext, this)
        val manager = LinearLayoutManager(this)
        manager.reverseLayout = true
        recyclerView!!.layoutManager = manager
        recyclerView.itemAnimator = null
        recyclerView!!.adapter = adapter
        adapter!!.startListening()
        adapter!!.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                recyclerView!!.smoothScrollToPosition(0)
            }
        })
    }

    fun sendMessageToUser(message: String?) {
        Utils.getChatroomReference(chatroomId).set(chatroomModel!!)
        val chatMessageModel = ChatmessageModel(message, Utils.currentUserId(), Timestamp.now())
        Utils.getChatroomMessageReference(chatroomId).add(chatMessageModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    messageInput!!.setText("")
                }
            }
    }

    val orCreateChatroomModel: Unit
        get() {
            Utils.getChatroomReference(chatroomId).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    chatroomModel = task.result?.toObject(ChatroomModel::class.java)

                    if (chatroomModel == null) {
                        // First time chat
                        val otherUserId = otherUser?.userId ?: "defaultUserId"
                        chatroomModel = ChatroomModel(
                            chatroomId!!,
                            listOf(Utils.currentUserId()!!, otherUserId)
                        )
                        Utils.getChatroomReference(chatroomId).set(chatroomModel!!)
                    }
                }
            }
        }
}