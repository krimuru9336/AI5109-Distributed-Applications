package com.example.chitchat

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.example.chitchat.adapter.ChatRecyclerAdapter
import com.example.chitchat.models.ChatmessageModel
import com.example.chitchat.models.ChatroomModel
import com.example.chitchat.models.MessageType
import com.example.chitchat.models.UserModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage


class ChatActivity : AppCompatActivity() {
    var otherUser: UserModel? = null
    var chatroomId: String? = null
    var userIds: ArrayList<String> = arrayListOf()
    var chatroomModel: ChatroomModel? = null
    var adapter: ChatRecyclerAdapter? = null
    lateinit var messageInput: EditText
    lateinit var sendMessageBtn: ImageButton
    lateinit var mediaBtn: ImageButton
    lateinit var backBtn: ImageButton
    lateinit var otherUsername: TextView
    lateinit var addUserBtn: ImageButton
    lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        //get UserModel
        otherUser = Utils.getUserModelFromIntent(intent)
        chatroomId = Utils.getChatroomIdFromIntent(intent)
        if (chatroomId == null) {
            chatroomId =
                otherUser!!.userId?.let { Utils.getChatroomId(Utils.currentUserId()!!, it) }
        } else {
            userIds = Utils.getChatroomUserIds(intent)!!
        }

        messageInput = findViewById(R.id.chat_message_input)
        sendMessageBtn = findViewById(R.id.message_send_btn)
        mediaBtn = findViewById(R.id.media_btn)
        backBtn = findViewById(R.id.back_btn)
        otherUsername = findViewById(R.id.other_username)
        addUserBtn = findViewById(R.id.add_user_btn)
        recyclerView = findViewById(R.id.chat_recycler_view)
        backBtn.setOnClickListener(View.OnClickListener { v: View? -> onBackPressed() })
        otherUsername.setText(otherUser!!.username)
        sendMessageBtn.setOnClickListener(View.OnClickListener { v: View? ->
            val message = messageInput.getText().toString().trim { it <= ' ' }
            if (message.isEmpty()) return@OnClickListener
            sendMessageToUser(message, MessageType.TEXT)
        })
        addUserBtn.setOnClickListener() {
            showAddUserDialog(this)
        }

        mediaBtn.setOnClickListener() {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("image/* video/*")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
            intent.addCategory(Intent.CATEGORY_OPENABLE)


            startActivityForResult(intent, 1)
        }

        setChatTitle()
        orCreateChatroomModel
        setupChatRecyclerView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (data != null && data.data != null) {
                val selectedMediaUri = data.data
                if (selectedMediaUri != null) {
                    val type = this.getContentResolver().getType(selectedMediaUri);
                    if (type!!.contains("image")) {
                        uploadToFirebaseStorage(selectedMediaUri, MessageType.IMAGE)
                    } else {
                        uploadToFirebaseStorage(selectedMediaUri, MessageType.VIDEO)
                    }
                }
            }
        }
    }


    //to utils?
    private fun uploadToFirebaseStorage(mediaUri: Uri, type: MessageType) {
        val storageReference = FirebaseStorage.getInstance().reference
        val mediaReference = storageReference.child("media").child(mediaUri.lastPathSegment!!)

        mediaReference.putFile(mediaUri)
            .addOnSuccessListener { taskSnapshot ->
                mediaReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    sendMessageToUser(downloadUrl, type)
                }
            }
            .addOnFailureListener { exception ->
            }
    }

    private fun setChatTitle() {
        if (userIds!!.count() > 2) {
            val stringBuilder = StringBuilder()
            Utils.getOtherUsersFromChatroom(userIds!!)
                .forEach { userRef ->
                    userRef.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val otherUserModel: UserModel? =
                                task.result?.toObject(UserModel::class.java)
                            if (otherUserModel != null) {
                                if (stringBuilder.isNotEmpty()) {
                                    stringBuilder.append(", ");
                                }
                                stringBuilder.append(otherUserModel.username)
                                otherUsername.setText(stringBuilder.toString());
                            }
                        }
                    }
                }
        }
    }

    private fun showAddUserDialog(
        context: Context,
    ) {
        val editText = EditText(context)

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Add user")
        builder.setView(editText)

        builder.setPositiveButton("Add") { _, _ ->
            val username = editText.text.toString()

            val addUserTask = Utils.getUserFromName(username).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val querySnapshot = task.result

                    if (querySnapshot != null && !querySnapshot.isEmpty) {
                        val userModel = querySnapshot.documents[0].toObject(UserModel::class.java)

                        if (userModel != null) {
                            Utils.getChatroomReference(chatroomId)
                                .get().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val chatroomModel =
                                            task.result?.toObject(ChatroomModel::class.java)

                                        if (chatroomModel != null) {
                                            val updatedUserIds =
                                                chatroomModel.userIds?.toMutableList()
                                            if (updatedUserIds != null) {
                                                userModel.userId?.let { updatedUserIds.add(it) }
                                            }

                                            val updatedChatroomModel = ChatroomModel(
                                                chatroomId,
                                                updatedUserIds
                                            )
                                            Utils.getChatroomReference(chatroomId)
                                                .set(updatedChatroomModel)
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        userIds =
                                                            updatedUserIds as ArrayList<String>
                                                        setChatTitle()
                                                    }
                                                }
                                        }
                                    }
                                }
                        }
                    }
                }
            }
        }
        builder.create().show()
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

    fun sendMessageToUser(message: String?, messageType: MessageType) {
        Utils.getChatroomReference(chatroomId).set(chatroomModel!!)
        val chatMessageModel =
            ChatmessageModel(message, Utils.currentUserId(), Timestamp.now(), messageType)
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