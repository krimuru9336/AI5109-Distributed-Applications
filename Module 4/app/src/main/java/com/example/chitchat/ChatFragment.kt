package com.example.chitchat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chitchat.adapter.RecentChatRecyclerAdapter
import com.example.chitchat.models.ChatroomModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions


class ChatFragment : Fragment() {
    var recyclerView: RecyclerView? = null
    var adapter: RecentChatRecyclerAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        recyclerView = view.findViewById(R.id.recyler_view)
        setupRecyclerView()
        return view
    }

    fun setupRecyclerView() {
        val query = Utils.allChatroomCollectionReference()
            .whereArrayContains("userIds", Utils.currentUserId()!!)
        val options: FirestoreRecyclerOptions<ChatroomModel> =
            FirestoreRecyclerOptions.Builder<ChatroomModel>()
                .setQuery(query, ChatroomModel::class.java).build()
        adapter = context?.let { RecentChatRecyclerAdapter(options, it) }
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        recyclerView!!.itemAnimator = null
        recyclerView!!.adapter = adapter
        adapter!!.startListening()
    }

    override fun onStart() {
        super.onStart()
        if (adapter != null) adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (adapter != null) adapter!!.stopListening()
    }

    override fun onResume() {
        super.onResume()
        if (adapter != null) adapter!!.notifyDataSetChanged()
    }
}