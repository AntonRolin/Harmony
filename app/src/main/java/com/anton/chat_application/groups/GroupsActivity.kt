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
    private lateinit var databaseGroupsRef: DatabaseReference
    private lateinit var groupListener: Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        verifyUserIsLoggedIn()

        super.onCreate(savedInstanceState)
        activityBinding = ActivityGroupsBinding.inflate(layoutInflater)
        val view = activityBinding.root
        setContentView(view)
        activityBinding.myGroupsGroupsRecyclerView.adapter = adapter

        setupMenuBar()
        fetchGroupsFromFirebaseDatabase()

        activityBinding.groupsSwipeRefreshLayout.setOnRefreshListener {
            refreshGroups()
        }

    }

    companion object {
        const val GROUP_KEY = "GROUP_KEY"
    }

    private fun fetchGroupsFromFirebaseDatabase() {
        databaseGroupsRef = FirebaseDatabase.getInstance("https://harmony-chatapp-default-rtdb.europe-west1.firebasedatabase.app").getReference("/groups")
        val userUid = FirebaseAuth.getInstance().uid

        groupListener = databaseGroupsRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                snapshot.children.forEach {
                    val group = it.getValue(Group::class.java) ?: return@forEach
                    it.child("/members/$userUid").getValue(Member::class.java) ?: return@forEach
                    val memberCount = it.child("/members").children.count()

                    adapter.add(GroupItem(group, memberCount))
                }

                adapter.setOnItemClickListener { item, view ->
                    val groupItem = item as GroupItem

                    val intent = Intent(view.context, SubjectsActivity::class.java)
                    intent.putExtra(GROUP_KEY, groupItem.group)
                    startActivity(intent)
                }
                activityBinding.groupsSwipeRefreshLayout.isRefreshing = false
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

    private fun refreshGroups() {
        Log.d(tagGroups, "Attempting to refresh groups")

        adapter.clear()
        fetchGroupsFromFirebaseDatabase()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        refreshGroups()

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_new_group -> {
                val intent = Intent(this, NewGroupActivity::class.java)
                startActivityForResult(intent, 0)
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