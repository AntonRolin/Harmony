package com.anton.chat_application

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.anton.chat_application.Models.GroupItem
import com.anton.chat_application.databinding.ActivityGroupsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupieAdapter

class GroupsActivity : AppCompatActivity() {
    private val tagGroups = "GroupsActivity"
    private lateinit var activityBinding: ActivityGroupsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityGroupsBinding.inflate(layoutInflater)
        val view = activityBinding.root
        setContentView(view)

        verifyUserIsLoggedIn()
        fetchGroups()

    }

    private fun fetchGroups(){
        val ref = FirebaseDatabase.getInstance("https://harmony-chatapp-default-rtdb.europe-west1.firebasedatabase.app").getReference("/groups")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupieAdapter()

                snapshot.children.forEach {
                    val group = it.getValue(Models.Group::class.java) ?: return@forEach
                    adapter.add(GroupItem(group))
                }

                activityBinding.myGroupsGroupsRecyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(tagGroups, "Error: ${error.message}")
            }
        })
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid

        if (uid == null){
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_groups, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_new_group -> {
                val intent = Intent(this, NewGroupActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}