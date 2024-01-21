package com.example.chitchat

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chitchat.adapter.SearchUserRecyclerAdapter
import com.example.chitchat.models.UserModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query


class SearchActivity : AppCompatActivity() {
    lateinit var searchInput: EditText
    lateinit var searchButton: ImageButton
    lateinit var backButton: ImageButton
    lateinit var recyclerView: RecyclerView
    var adapter: SearchUserRecyclerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        searchInput = findViewById<EditText>(R.id.seach_username_input)
        searchButton = findViewById<ImageButton>(R.id.search_user_btn)
        backButton = findViewById<ImageButton>(R.id.back_btn)
        recyclerView = findViewById<RecyclerView>(R.id.search_user_recycler_view)
        searchInput.requestFocus()
        backButton.setOnClickListener(View.OnClickListener { v: View? -> onBackPressed() })
        searchButton.setOnClickListener(View.OnClickListener { v: View? ->
            val searchTerm = searchInput.getText().toString()
            if (searchTerm.isEmpty()) {
                searchInput.setError("Invalid Username")
                return@OnClickListener
            }
            setupSearchRecyclerView(searchTerm)
        })
    }

    fun setupSearchRecyclerView(searchTerm: String) {
        val query: Query = Utils.allUserCollectionReference()
            .whereGreaterThanOrEqualTo("username", searchTerm)
            .whereLessThanOrEqualTo("username", searchTerm + '\uf8ff')
        val options = FirestoreRecyclerOptions.Builder<UserModel>()
            .setQuery(query, UserModel::class.java).build()
        adapter = SearchUserRecyclerAdapter(options, applicationContext)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
       recyclerView!!.itemAnimator = null
        recyclerView!!.adapter = adapter

        adapter?.startListening()
    }

    override fun onStart() {
        super.onStart()
        if (adapter != null) adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (adapter != null) adapter?.stopListening()
    }

    override fun onResume() {
        super.onResume()
        if (adapter != null) adapter?.startListening()
    }
}











