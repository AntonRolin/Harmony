package com.anton.chat_application.subjects

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.anton.chat_application.databinding.ActivityAddMemberBinding
import com.anton.chat_application.groups.GroupsActivity
import com.anton.chat_application.models.Group
import com.anton.chat_application.models.Member
import com.anton.chat_application.models.User
import com.anton.chat_application.models.UserItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupieAdapter

class AddMemberActivity : AppCompatActivity() {
    private val tagAddMember = "AddMemberActivity"
    private lateinit var activityBinding: ActivityAddMemberBinding
    private lateinit var group: Group
    private val adapter = GroupieAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityAddMemberBinding.inflate(layoutInflater)
        val view = activityBinding.root
        setContentView(view)

        activityBinding.recyclerviewAddMember.adapter = adapter
        group = intent.getParcelableExtra(GroupsActivity.GROUP_KEY)!!

        setupMenuBar()
        fetchUsersFromFirebaseDatabase()
    }

    private fun fetchUsersFromFirebaseDatabase() {
        val ref = FirebaseDatabase.getInstance("https://harmony-chatapp-default-rtdb.europe-west1.firebasedatabase.app").getReference("/users")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                snapshot.children.forEach {
                    val user = it.getValue(User::class.java) ?: return@forEach

                    if (user.uid == FirebaseAuth.getInstance().uid) return@forEach

                    adapter.add(UserItem(user))
                }
                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                    addMemberToGroupInFirebaseDatabase(userItem.user)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(tagAddMember, "Error: ${error.message}")
            }

        })
    }

    private fun addMemberToGroupInFirebaseDatabase(user: User) {
        val ref = FirebaseDatabase.getInstance("https://harmony-chatapp-default-rtdb.europe-west1.firebasedatabase.app").getReference("/groups/${group.uid}/members/${user.uid}")
        val member = Member(user.uid)

        ref.setValue(member)
            .addOnSuccessListener {
                Log.d(tagAddMember, "Successfully added member with uid: ${member.userUid}")
                finish()
            }
            .addOnFailureListener {
                Log.d(tagAddMember, "Error: ${it.message}")
            }
    }

    private fun setupMenuBar(){
        supportActionBar?.title = "Add Members"
        supportActionBar?.setDisplayShowTitleEnabled(true)
    }
}