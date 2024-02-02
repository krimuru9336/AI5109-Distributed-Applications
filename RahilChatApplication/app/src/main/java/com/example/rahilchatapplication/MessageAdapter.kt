package com.example.rahilchatapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MessageAdapter(private val context: Context, private val messageList: ArrayList<Message>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_RECEIVE = 1
    private val ITEM_SENT = 2
    private lateinit var mDbRef: DatabaseReference
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if(viewType == 1){
            val view: View = LayoutInflater.from(context).inflate(R.layout.receive, parent, false)
            ReceiveViewHolder(view)
        }else{
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent, parent, false)
            SentViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]

        if (holder.javaClass == SentViewHolder::class.java){
            val viewHolder = holder as SentViewHolder
            val sentMessageLayout: View = viewHolder.itemView.findViewById(R.id.sentMessageLayout)
            viewHolder.sentMessage.text = currentMessage.message
            viewHolder.time.text = currentMessage.time
            sentMessageLayout.setOnLongClickListener {
                showPopupMenu(it, currentMessage)
                true
            }

        }else {
            val viewHolder = holder as ReceiveViewHolder
            viewHolder.receiveMessage.text = currentMessage.message
            viewHolder.time.text = currentMessage.time
        }
    }

    private fun showPopupMenu(view: View, currentMessage: Message) {
        val popupMenu = PopupMenu(view.context, view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.popup_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_edit -> {
                    showEditDialog(currentMessage)
                    true
                }
                R.id.menu_delete -> {
                    deleteMessage(currentMessage)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun showEditDialog(message: Message) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("Edit Message")

        val input = EditText(context)
        input.setText(message.message)
        alertDialog.setView(input)

        alertDialog.setPositiveButton("OK") { _, _ ->
            val editedMessage = input.text.toString()
            if (editedMessage.isNotEmpty()) {
                editMessage(message, editedMessage)
            } else {
                Toast.makeText(context, "Message cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        alertDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        alertDialog.show()
    }

    private fun editMessage(message: Message, editedMessage: String) {
        val contentToEdit = message.message
        val senderRoom = message.senderRoomId
        val receiverRoom = message.receiverRoomId
        mDbRef = FirebaseDatabase.getInstance().reference
        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .orderByChild("message").equalTo(contentToEdit)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        // Update the message content in the database
                        childSnapshot.ref.child("message").setValue(editedMessage)
                    }
                    Toast.makeText(context, "Message edited successfully", Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to edit message", Toast.LENGTH_SHORT).show()
                }
            })

        mDbRef.child("chats").child(receiverRoom!!).child("messages")
            .orderByChild("message").equalTo(contentToEdit)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        // Update the message content in the database
                        childSnapshot.ref.child("message").setValue(editedMessage)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to edit message", Toast.LENGTH_SHORT).show()
                }
            })


    }

    private fun deleteMessage(currentMessage: Message) {
        val contentToDelete  =  currentMessage.message
        val senderRoom = currentMessage.senderRoomId
        mDbRef = FirebaseDatabase.getInstance().reference
        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .orderByChild("message").equalTo(contentToDelete)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        childSnapshot.ref.removeValue()
                    }
                    Toast.makeText(context, "Message deleted successfully", Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to delete message", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        return if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            ITEM_SENT
        }else{
            ITEM_RECEIVE
        }
    }

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sentMessage: TextView = itemView.findViewById(R.id.txt_sent_message)
        val time: TextView =  itemView.findViewById(R.id.timestampTextViewSent)
    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val receiveMessage: TextView = itemView.findViewById(R.id.txt_receive_message)
        val time: TextView =  itemView.findViewById(R.id.timestampTextViewReceived)
    }
}