package com.anton.chat_application.subjects

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.anton.chat_application.R
import com.anton.chat_application.databinding.ActivitySubjectsBinding
import com.anton.chat_application.groups.GroupsActivity
import com.anton.chat_application.messages.MessageLogActivity
import com.anton.chat_application.models.ChatMessage
import com.anton.chat_application.models.Group
import com.anton.chat_application.models.Subject
import com.anton.chat_application.models.SubjectItem
import com.google.firebase.database.*
import com.xwray.groupie.GroupieAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class SubjectsActivity : AppCompatActivity() {
    private val tagSubjects = "SubjectsActivity"
    private lateinit var group: Group
    private lateinit var activityBinding: ActivitySubjectsBinding
    private val adapter = GroupieAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivitySubjectsBinding.inflate(layoutInflater)
        val view = activityBinding.root
        setContentView(view)

        activityBinding.groupSubjectsRecyclerView.adapter = adapter
        group = intent.getParcelableExtra(GroupsActivity.GROUP_KEY)!!

        setupMenuBar()
        fetchSubjectsFromFirebaseDatabase()


    }

    companion object {
        const val SUBJECT_KEY = "SUBJECT_KEY"
        val standardMessage: ChatMessage =
            ChatMessage("-1", "No messages to show", "-1", "Username", "-1", -1)
    }

    private fun fetchSubjectsFromFirebaseDatabase() {
        val itemList: MutableMap<String, SubjectItem> = mutableMapOf()
        val ref =
            FirebaseDatabase.getInstance("https://harmony-chatapp-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("/groups/${group.uid}/subjects")
        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val subject = snapshot.getValue(Subject::class.java) ?: return
                val hasMessages = snapshot.child("/messages").hasChildren()
                var latestMessage = standardMessage

                if (hasMessages)
                    latestMessage = snapshot.child("/messages").children.last()
                        .getValue(ChatMessage::class.java) ?: return

                val subjectItemInMap = SubjectItem(subject, latestMessage)
                adapter.add(subjectItemInMap)

                val key = subject.uid
                itemList[key] = subjectItemInMap

                adapter.setOnItemClickListener { item, view ->
                    val subjectItem = item as SubjectItem

                    val intent = Intent(view.context, MessageLogActivity::class.java)
                    intent.putExtra(SUBJECT_KEY, subjectItem.subject)
                    intent.putExtra(GroupsActivity.GROUP_KEY, group)
                    startActivity(intent)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(tagSubjects, "Subject changed: ${snapshot.value}")
                Thread.sleep(500)

                val subject = snapshot.getValue(Subject::class.java) ?: return
                val latestMessage =
                    snapshot.child("/messages").children.last().getValue(ChatMessage::class.java)
                        ?: return

                val key = subject.uid
                val subjectItem = itemList[key] as SubjectItem

                subjectItem.latestMessage = latestMessage
                subjectItem.notifyChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(tagSubjects, "Error: ${error.message}")
            }
        })
    }

    private fun setupMenuBar() {
        supportActionBar?.title = group.groupName
        supportActionBar?.setDisplayShowTitleEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_subjects, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_member -> {
                val intent = Intent(this, AddMemberActivity::class.java)
                intent.putExtra(GroupsActivity.GROUP_KEY, group)
                startActivity(intent)
            }
            R.id.menu_new_subject -> {
                val intent = Intent(this, NewSubjectActivity::class.java)
                intent.putExtra(GroupsActivity.GROUP_KEY, group)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}