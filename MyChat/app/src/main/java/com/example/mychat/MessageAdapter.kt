package com.example.mychat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        val ITEM_RECIEVE = 1;
    val ITEM_SENT =2;


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == 1){
            //inflate recieve
            val view: View = LayoutInflater.from(context).inflate(R.layout.receive,parent,false)
            return ReceiveViewholder(view)
        }else{
            //inflate sent
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent,parent,false)
            return SentViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]

        if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            return ITEM_SENT
            }else{
                return ITEM_RECIEVE
        }
    }

    override fun getItemCount(): Int {

        return messageList.size

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentMessage = messageList[position]


        if(holder.javaClass == SentViewHolder::class.java){
            //do the stuff for sent view holder

            val viewHolder = holder as SentViewHolder
            holder.sentMessage.text = currentMessage.message
            holder.timestamp.text = currentMessage.timestamp

        }else{
            //do the stuff for receive view holder
            val viewHolder = holder as ReceiveViewholder
            holder.receiveMessage.text = currentMessage.message
            holder.timestamp.text = currentMessage.timestamp
        }
    }
    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val sentMessage = itemView.findViewById<TextView>(R.id.txt_sent_message)
        val timestamp = itemView.findViewById<TextView>(R.id.txt_sent_timestamp)
    }


    class ReceiveViewholder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val receiveMessage = itemView.findViewById<TextView>(R.id.txt_receive_message)
        val timestamp = itemView.findViewById<TextView>(R.id.txt_receive_timestamp)
    }

}
