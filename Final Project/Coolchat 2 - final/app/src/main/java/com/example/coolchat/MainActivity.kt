package com.example.coolchat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coolchat.ui.theme.CoolChatTheme
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import com.example.coolchat.databinding.ActivityMainBinding
import com.example.coolchat.model.FriendlyMessage
import com.google.firebase.BuildConfig
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var manager: LinearLayoutManager

/*   private val openDocument = registerForActivityResult(MyOpenDocumentContract()) { uri ->
        uri?.let { onImageSelected(it) }
    }*/

   private val openDocument = registerForActivityResult(MyOpenDocumentContract()) { uri ->
        uri?.let {
            val mimeType = contentResolver.getType(it)
            when {
                mimeType?.startsWith("image") == true -> onImageSelected(it)
                mimeType?.startsWith("video") == true -> onVideoSelected(it)
                else -> Log.e(TAG, "Unsupported file type")
            }
        }
    }


    // Firebase instance variables
    private lateinit var auth: FirebaseAuth

    private lateinit var db: FirebaseDatabase
    private lateinit var adapter: FriendlyMessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoolChatTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Initialize Firebase Auth and check if the user is signed in
        auth = Firebase.auth
        if (auth.currentUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }

        // Initialize Realtime Database and FirebaseRecyclerAdapter
        db = Firebase.database
        val messagesRef = db.reference.child(MESSAGES_CHILD)


        val options = FirebaseRecyclerOptions.Builder<FriendlyMessage>()
            .setQuery(messagesRef, FriendlyMessage::class.java)
            .build()
        adapter = FriendlyMessageAdapter(options, getUserName(),
            onDeleteClick = { item ->
                item.id?.let {
                    db.reference.child(MESSAGES_CHILD).child(it).removeValue()
                }
            },
            onEditClick = { item ->
                showEditDialog(item)
            }
        )
        binding.progressBar.visibility = ProgressBar.INVISIBLE
        manager = LinearLayoutManager(this)
        manager.stackFromEnd = true
        binding.messageRecyclerView.layoutManager = manager
        binding.messageRecyclerView.adapter = adapter

        // Scroll down when a new message arrives
        adapter.registerAdapterDataObserver(
            MyScrollToBottomObserver(binding.messageRecyclerView, adapter, manager)
        )

        // Disable the send button when there's no text in the input field
        binding.messageEditText.addTextChangedListener(MyButtonObserver(binding.sendButton))

        // When the send button is clicked, send a text message
        binding.sendButton.setOnClickListener {
            val dbItem = db.reference.child(MESSAGES_CHILD).push()
            val friendlyMessage = FriendlyMessage(
                dbItem.key,
                binding.messageEditText.text.toString(),
                getUserName(),
                getPhotoUrl(),
                null /* no image */,
                null,
                System.currentTimeMillis()
            )
            dbItem.setValue(friendlyMessage)
            binding.messageEditText.setText("")
        }

        // When the image button is clicked, launch the image picker
//        binding.addMessageImageView.setOnClickListener {
//            openDocument.launch(arrayOf("image/*"))
//        }

        binding.addMessageImageView.setOnClickListener {
            openDocument.launch(arrayOf("image/*", "video/*"))
        }

    }

    private fun showEditDialog(item: FriendlyMessage) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
        val input = EditText(this)
        input.setText(item.text)

        builder.setView(input)
        builder.setPositiveButton("OK") { _, _ ->
            val editedText = input.text.toString()
            item.id?.let { messageId ->
                db.reference.child(MESSAGES_CHILD).child(messageId).child("text").setValue(editedText)
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }


    public override fun onStart() {
        super.onStart()
        // Check if user is signed in.
        if (auth.currentUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }
    }

    public override fun onPause() {
        adapter.stopListening()
        super.onPause()
    }

    public override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out_menu -> {
                signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onImageSelected(uri: Uri) {
        Log.d(TAG, "Uri: $uri")
        val user = auth.currentUser
        val tempMessage = FriendlyMessage(UUID.randomUUID().toString(),
        null, getUserName(), getPhotoUrl(), LOADING_IMAGE_URL)
        db.reference
            .child(MESSAGES_CHILD)
            .push()
            .setValue(
                tempMessage,
                DatabaseReference.CompletionListener { databaseError, databaseReference ->
                    if (databaseError != null) {
                        Log.w(
                            TAG, "Unable to write message to database.",
                            databaseError.toException()
                        )
                        return@CompletionListener
                    }

                    // Build a StorageReference and then upload the file
                    val key = databaseReference.key
                    val storageReference = Firebase.storage
                        .getReference(user!!.uid)
                        .child(key!!)
                        .child(uri.lastPathSegment!!)
                    putImageInStorage(storageReference, uri, key)
                })
    }

    private fun putImageInStorage(storageReference: StorageReference, uri: Uri, key: String?) {
        // Upload the image to Cloud Storage
        storageReference.putFile(uri)
            .addOnSuccessListener(
                this
            ) { taskSnapshot -> // After the image loads, get a public downloadUrl for the image
                // and add it to the message.
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        val friendlyMessage =
                            FriendlyMessage(UUID.randomUUID().toString(),null, getUserName(), getPhotoUrl(), uri.toString())
                        db.reference
                            .child(MESSAGES_CHILD)
                            .child(key!!)
                            .setValue(friendlyMessage)
                        Log.w(TAG, "putImageInStorage: image link: $uri")
                    }
                Log.w(TAG, "putImageInStorage: upload was successful", )
            }
            .addOnFailureListener(this) { e ->
                Log.w(
                    TAG,
                    "Image upload task was unsuccessful.",
                    e
                )
            }
    }

    private fun onVideoSelected(uri: Uri) {
        val user = auth.currentUser
        val tempMessage = FriendlyMessage(id = UUID.randomUUID().toString(),
            name = getUserName(),
            photoUrl = getPhotoUrl(),
            imageUrl = LOADING_IMAGE_URL)
        db.reference
            .child(MESSAGES_CHILD)
            .push()
            .setValue(
                tempMessage,
                DatabaseReference.CompletionListener { databaseError, databaseReference ->
                    if (databaseError != null) {
                        Log.w(
                            TAG, "Unable to write message to database.",
                            databaseError.toException()
                        )
                        return@CompletionListener
                    }

                    // Build a StorageReference and then upload the file
                    val key = databaseReference.key
                    val storageReference = Firebase.storage
                        .getReference(user!!.uid)
                        .child(key!!)
                        .child(uri.lastPathSegment!!)
                    putVideoInStorage(storageReference, uri, key)
                })
    }

    private fun putVideoInStorage(storageReference: StorageReference, uri: Uri, key: String?) {
        // Upload the video to Cloud Storage
        storageReference.putFile(uri)
            .addOnSuccessListener(
                this
            ) { taskSnapshot -> // After the video loads, get a public downloadUrl for the video
                // and add it to the message.
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        val friendlyMessage =
                            FriendlyMessage(id = UUID.randomUUID().toString(),
                                name = getUserName(),
                                photoUrl = getPhotoUrl(),
                                videoUrl = uri.toString())
                        db.reference
                            .child(MESSAGES_CHILD)
                            .child(key!!)
                            .setValue(friendlyMessage)
                        Log.w(TAG, "putVideoInStorage: video link: $uri")
                    }
                Log.w(TAG, "putVideoInStorage: upload was successful")
            }
            .addOnFailureListener(this) { e ->
                Log.w(
                    TAG,
                    "Video upload task was unsuccessful.",
                    e
                )
            }
    }

    private fun signOut() {
        AuthUI.getInstance().signOut(applicationContext)
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }

    private fun getPhotoUrl(): String? {
        val user = auth.currentUser
        return user?.photoUrl?.toString()
    }

    private fun getUserName(): String? {
        val user = auth.currentUser
        return if (user != null) {
            user.displayName
        } else ANONYMOUS
    }

    companion object {
        private const val TAG = "MainActivity"
        const val MESSAGES_CHILD = "messages"
        const val ANONYMOUS = "anonymous"
        private const val LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif"
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CoolChatTheme {
        Greeting("Android")
    }
}