package com.example.mychat

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GroupChatActivity : AppCompatActivity() {
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference
    private lateinit var editDialog: AlertDialog
    private lateinit var groupChatRoomId: String
    private var fileType: String? = null
    private val PICK_IMAGE_VIDEO_REQUEST = 1

    var receiverRoom: String? = null
    var senderRoom: String? = null
    var editMessageInput: EditText? = null
    var groupRoom: String? = null

    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)
        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        val sendMediaButton: ImageButton = findViewById(R.id.sendMediaButton)
        sendMediaButton.setOnClickListener {
            openGallery()
        }
        groupChatRoomId = intent.getStringExtra("groupId") ?: ""
        mDbRef = FirebaseDatabase.getInstance().getReference().child("group_chats").child(groupChatRoomId)

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid
        groupRoom = "messages"



        supportActionBar?.title = name


        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messagebox)
        sendButton = findViewById(R.id.sentButton)

        messageList = ArrayList()
        // Adding delete functionality
        val deleteListener = object : DeleteMessageListener {
            override fun onDeleteMessage(position: Int) {
                deleteMessage(position)
            }
        }
        val editListener = object : EditMessageListener {
            override fun onEditMessage(position: Int) {
                editMessage(position)
            }
        }

        messageAdapter = MessageAdapter(this, messageList, deleteListener, editListener)



        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        //logic for adding data to recycler view
        mDbRef.child(groupRoom!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()

                    for(postSnapshot in snapshot.children){
                        val message = postSnapshot.getValue(Message::class.java)
                        if (message != null) {
                            message.id = postSnapshot.key
                        }
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        //adding the message to the database
        sendButton.setOnClickListener {

            val message = messageBox.text.toString()
            val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val date = Date(System.currentTimeMillis())
            val messageObject = Message(message, senderUid, dateFormat.format(date), "text")

            mDbRef.child(groupRoom!!).push()
                .setValue(messageObject)
            messageBox.setText("")

        }

        // Initialize the edit dialog
        val editView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_message, null)
        editMessageInput = editView.findViewById<EditText>(R.id.editMessageInput)

        editDialog = AlertDialog.Builder(this)
            .setTitle("Edit Message")
            .setView(editView)
            .setPositiveButton("Save") { _, _ ->
                val editedMessage = editMessageInput?.text.toString()
                if (editedMessage.isNotEmpty()) {
                    // Handle saving the edited message to the database
                    saveEditedMessage(selectedMessagePosition, editedMessage)
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/* video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image or Video"), PICK_IMAGE_VIDEO_REQUEST)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val selectedMediaUri: Uri = data.data!!
            val fileType = contentResolver.getType(selectedMediaUri)

            if (fileType?.startsWith("image/") == true || fileType?.startsWith("video/") == true) {
                // Upload the selected image or video to Firebase Storage
                uploadMedia(selectedMediaUri, fileType)
            } else {
                Toast.makeText(this, "Please select a valid image or video", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadMedia(mediaUri: Uri, fileType: String) {
        val storageReference = FirebaseStorage.getInstance().reference
        val mediaRef = storageReference.child("media").child("${System.currentTimeMillis()}.$fileType")
        mediaRef.putFile(mediaUri)
            .addOnSuccessListener { taskSnapshot ->
                // Get the download URL of the uploaded media
                mediaRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    // Create a Message object for the uploaded media
                    val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    val date = Date(System.currentTimeMillis())
                    val senderUid = FirebaseAuth.getInstance().currentUser?.uid
                    val messageObject = Message(downloadUrl, senderUid, dateFormat.format(date), mediaType = fileType)

                    // Save the message to the database
                    sendMessageToDatabase(messageObject)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to upload media: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendMessageToDatabase(message:Message){
        // Save the message to the sender's room
        mDbRef.child(groupRoom!!).push()
            .setValue(message)
            .addOnSuccessListener {

            }
            .addOnFailureListener {
                // Handle failure
            }

    }


    private var selectedMessagePosition: Int = -1
    private fun editMessage(position: Int) {
        selectedMessagePosition = position
        val selectedMessage = messageList[position]
        editDialog.show()
        editMessageInput?.setText(selectedMessage.message)
    }
    private fun saveEditedMessage(position: Int, editedMessage: String) {
        // Handle saving the edited message to the database
        val messageId = messageList[position].id
        if (messageId != null) {
            mDbRef.child(groupRoom!!).child(messageId)
                .child("message").setValue(editedMessage)
                .addOnSuccessListener {
                    // Handle success
                }
                .addOnFailureListener {
                    // Handle failure
                }
        }
    }

    private fun deleteMessage(position: Int) {
        if (position in 0 until messageList.size) {
            val messageId = messageList[position].id // Assuming your Message class has an 'id' property

            // Remove from local list
            messageList.removeAt(position)
            messageAdapter.notifyItemRemoved(position)

            // Remove from the database
            if (messageId != null) {
                mDbRef.child(groupRoom!!).child(messageId)
                    .removeValue().addOnSuccessListener {
                        // Handle success
                    }.addOnFailureListener {
                        // Handle failure
                    }
            }

            if (messageId != null) {
                mDbRef.child(groupRoom!!).child(messageId)
                    .removeValue().addOnSuccessListener {
                        // Handle success
                    }.addOnFailureListener {
                        // Handle failure
                    }
            }
        }
    }

}
