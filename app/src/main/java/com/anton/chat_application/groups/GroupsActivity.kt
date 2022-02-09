package com.anton.chat_application.groups

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.anton.chat_application.R
import com.anton.chat_application.databinding.ActivityGroupsBinding
import com.anton.chat_application.loginregister.RegisterActivity
import com.anton.chat_application.models.Group
import com.anton.chat_application.models.GroupItem
import com.anton.chat_application.models.Member
import com.anton.chat_application.subjects.SubjectsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupieAdapter

class GroupsActivity : AppCompatActivity() {
    private val tagGroups = "GroupsActivity"
    private lateinit var activityBinding: ActivityGroupsBinding
    private val adapter = GroupieAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        verifyUserIsLoggedIn()

        super.onCreate(savedInstanceState)
        activityBinding = ActivityGroupsBinding.inflate(layoutInflater)
        val view = activityBinding.root
        setContentView(view)
        activityBinding.myGroupsGroupsRecyclerView.adapter = adapter

        setupMenuBar()
        fetchGroupsFromFirebaseDatabase()

    }

    companion object {
        const val GROUP_KEY = "GROUP_KEY"
    }

    private fun fetchGroupsFromFirebaseDatabase() {
        val ref = FirebaseDatabase.getInstance("https://harmony-chatapp-default-rtdb.europe-west1.firebasedatabase.app").getReference("/groups")
        val userUid = FirebaseAuth.getInstance().uid

        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val group = snapshot.getValue(Group::class.java) ?: return

                snapshot.child("/members/$userUid").getValue(Member::class.java) ?: return

                val memberCount = snapshot.child("/members").children.count()

                adapter.add(GroupItem(group, memberCount))

                adapter.setOnItemClickListener { item, view ->
                    val groupItem = item as GroupItem

                    val intent = Intent(view.context, SubjectsActivity::class.java)
                    intent.putExtra(GROUP_KEY, groupItem.group)
                    startActivity(intent)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

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

    private fun setupMenuBar(){
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.title = "My Groups"
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setLogo(R.drawable.logo_square)
        supportActionBar?.setDisplayUseLogoEnabled(true)
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