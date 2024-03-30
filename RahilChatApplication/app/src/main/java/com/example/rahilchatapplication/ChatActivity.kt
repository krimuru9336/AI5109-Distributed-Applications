package com.example.rahilchatapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    var receiverRoom: String? = null
    var senderRoom: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentLocalDateTime(): LocalDateTime {
        return LocalDateTime.now()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatLocalDateTime(localDateTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm")
        return localDateTime.format(formatter)
    }

    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        mDbRef = FirebaseDatabase.getInstance().getReference()
        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        supportActionBar?.title = name

        chatRecyclerView = findViewById<RecyclerView>(R.id.chatRecyclerView)
        chatRecyclerView.isNestedScrollingEnabled = false;
        messageBox = findViewById<EditText>(R.id.messageBox)
        sendButton = findViewById<ImageView>(R.id.btnSendMessage)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for(postSnapshot in snapshot.children){
                        val message = postSnapshot.getValue(Message::class.java)
                        if (message != null) {
                            messageList.add(message)
                        }
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        sendButton.setOnClickListener{
            val message = messageBox.text.toString()
            val currentDateTime = getCurrentLocalDateTime()
            val formattedDateTime = formatLocalDateTime(currentDateTime)
            val messageObject = Message(message, senderUid, formattedDateTime, senderRoom, receiverRoom)
            mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                .setValue((messageObject)).addOnSuccessListener {
                    mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue((messageObject))
                }
            messageBox.setText("")

        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.create_group) {
            val intent = Intent(this, CreateGroup::class.java)
            finish()
            startActivity(intent)
            return true;
        } else if(item.itemId == R.id.list_groups) {
            val intent = Intent(this, GroupList::class.java)
            finish()
            startActivity(intent)
            return true;
        }else if(item.itemId == R.id.chat_list) {
            val intent = Intent(this, MainActivity::class.java)
            finish()
            startActivity(intent)
            return true;
        }

        return true;
    }
}