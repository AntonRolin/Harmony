package com.anton.chat_application.messages

import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.anton.chat_application.AppBase.Companion.CHANNEL_MESSAGES_ID
import com.anton.chat_application.databinding.ActivityMessageLogBinding
import com.anton.chat_application.groups.GroupsActivity
import com.anton.chat_application.models.*
import com.anton.chat_application.subjects.SubjectsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupieAdapter

class MessageLogActivity : AppCompatActivity() {
    private val tagMessageLog = "MessageLogActivity"
    private lateinit var activityBinding: ActivityMessageLogBinding
    private lateinit var subject: Subject
    private lateinit var group: Group
    private lateinit var currentUser: User
    private val adapter = GroupieAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityMessageLogBinding.inflate(layoutInflater)
        val view = activityBinding.root
        setContentView(view)

        activityBinding.recyclerviewChatlog.adapter = adapter
        subject = intent.getParcelableExtra(SubjectsActivity.SUBJECT_KEY)!!
        group = intent.getParcelableExtra(GroupsActivity.GROUP_KEY)!!

        setupMenuBar()
        getCurrentUser()
        listenForMessages()

        activityBinding.sendButtonChatlog.setOnClickListener {
            performSendMessage()
        }

    }

    private fun listenForMessages() {
        val ref = FirebaseDatabase.getInstance("https://harmony-chatapp-default-rtdb.europe-west1.firebasedatabase.app").getReference("/groups/${group.uid}/subjects/${subject.uid}/messages").limitToLast(1000)

        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return

                if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                    adapter.add(ChatToItem(chatMessage))
                }
                else {
                    adapter.add(ChatFromItem(chatMessage))
                }
                activityBinding.recyclerviewChatlog.smoothScrollToPosition(adapter.itemCount - 1)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(tagMessageLog, "Error: ${error.message}")
            }
        })
    }

    private fun performSendMessage() {
        val text = activityBinding.enterMessageEdittextChatlog.text.toString()
        if (text.isEmpty()) return
        val userUid = FirebaseAuth.getInstance().uid.toString()
        val ref = FirebaseDatabase.getInstance("https://harmony-chatapp-default-rtdb.europe-west1.firebasedatabase.app").getReference("/groups/${group.uid}/subjects/${subject.uid}/messages").push()
        val id = ref.key.toString()
        val chatMessage = ChatMessage(id, text, userUid, currentUser.username, currentUser.profileImageUrl, Calendar.getInstance().timeInMillis)

        ref.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(tagMessageLog, "Saved message to Firebase Database with id: $id")

                activityBinding.enterMessageEdittextChatlog.text.clear()
            }
            .addOnFailureListener {
                Log.d(tagMessageLog, "Error: ${it.message}")
            }
    }

    private fun getCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid.toString()
        val ref = FirebaseDatabase.getInstance("https://harmony-chatapp-default-rtdb.europe-west1.firebasedatabase.app").getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java) ?: return
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(tagMessageLog, "Error: ${error.message}")
            }
        })
    }

    private fun setupMenuBar() {
        supportActionBar?.title = subject.subjectName
        supportActionBar?.setDisplayShowTitleEnabled(true)
    }
}