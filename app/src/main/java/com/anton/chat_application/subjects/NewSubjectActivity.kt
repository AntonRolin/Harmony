package com.anton.chat_application.subjects

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.anton.chat_application.GeneralFunctions
import com.anton.chat_application.databinding.ActivityNewSubjectBinding
import com.anton.chat_application.groups.GroupsActivity
import com.anton.chat_application.models.Group
import com.anton.chat_application.models.Subject
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class NewSubjectActivity : AppCompatActivity() {
    private val tagNewSubject = "NewSubjectActivity"
    private lateinit var activityBinding: ActivityNewSubjectBinding
    private var subjectName = ""
    private lateinit var group: Group

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityNewSubjectBinding.inflate(layoutInflater)
        val view = activityBinding.root
        setContentView(view)

        group = intent.getParcelableExtra(GroupsActivity.GROUP_KEY)!!

        setupMenuBar()

        activityBinding.createSubjectButton.setOnClickListener {
            performCreateSubject()
        }
    }

    private fun performCreateSubject() {
        subjectName = activityBinding.subjectNameEditText.text.toString()

        if (subjectName.isEmpty()) {
            GeneralFunctions().generateSnackBar(this, activityBinding.root, "Please enter a name for your subject")
            return
        }
        if (subjectName.length > 25) {
            GeneralFunctions().generateSnackBar(this, activityBinding.root, "Subject name is too long (max 25)")
            return
        }
        Log.d(tagNewSubject, "Subject name is: $subjectName")

        saveSubjectToFirebaseDatabase()
    }

    private fun saveSubjectToFirebaseDatabase() {
        val uid = UUID.randomUUID().toString()
        val ref = FirebaseDatabase.getInstance("https://harmony-chatapp-default-rtdb.europe-west1.firebasedatabase.app").getReference("/groups/${group.uid}/subjects/$uid")

        val subject = Subject(uid, subjectName, group.uid)

        ref.setValue(subject)
            .addOnSuccessListener {
                Log.d(tagNewSubject, "Saved subject to Firebase Database")
                finish()
            }
            .addOnFailureListener {
                Log.d(tagNewSubject, "Error: ${it.message}")
            }
    }

    private fun setupMenuBar() {
        supportActionBar?.title = "Create Subject"
        supportActionBar?.setDisplayShowTitleEnabled(true)
    }
}