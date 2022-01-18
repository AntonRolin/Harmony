package com.anton.chat_application

import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.anton.chat_application.databinding.ActivityNewGroupBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class NewGroupActivity : AppCompatActivity() {
    private val tagNewGroup = "NewGroupActivity"
    private lateinit var activityBinding: ActivityNewGroupBinding
    private var selectedPhotoUri: Uri? = null
    private var imageDownloadUrl: String = ""
    private var groupName: String = ""
    private var standardGroupImageUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityNewGroupBinding.inflate(layoutInflater)
        val view = activityBinding.root
        setContentView(view)

        setupMenuBar()
        fetchStandardGroupImageFromFirebaseDatabase()

        activityBinding.createGroupNewGroupButton.setOnClickListener {
            performCreateGroup()
        }

        activityBinding.selectPhotoNewGroupButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    private fun performCreateGroup() {
        groupName = activityBinding.groupNameNewGroupEditText.text.toString()

        if (groupName.isEmpty()) {
            GeneralFunctions().generateSnackBar(this, activityBinding.root, "Please enter a name for your group")
            return
        }
        Log.d(tagNewGroup, "Group name is: $groupName")

        uploadImageToFirebaseStorage()
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) {
            saveGroupToFirebaseDatabase()
            return
        }

        Log.d(tagNewGroup, "Starting upload")
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/groupPhotos/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener { it ->
                Log.d(tagNewGroup, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(tagNewGroup, "File location: $it")
                    imageDownloadUrl = it.toString()
                    saveGroupToFirebaseDatabase()
                }
                    .addOnFailureListener {
                        Log.d(tagNewGroup, "Error: ${it.message}")
                    }
            }
            .addOnFailureListener {
                Log.d(tagNewGroup, "Error: ${it.message}")
            }
    }

    private fun saveGroupToFirebaseDatabase() {
        val uid = UUID.randomUUID().toString()
        val ref = FirebaseDatabase.getInstance("https://harmony-chatapp-default-rtdb.europe-west1.firebasedatabase.app").getReference("/groups/$uid")
        val imageUrl = when (imageDownloadUrl) {
            ""   -> standardGroupImageUrl
            else -> imageDownloadUrl
        }
        Log.d(tagNewGroup, "ImageURL: $imageUrl")
        val group = Models.Group(groupName, imageUrl)

        ref.setValue(group)
            .addOnSuccessListener {
                Log.d(tagNewGroup, "Saved group to Firebase Database")

                val intent = Intent(this, GroupsActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d(tagNewGroup, "Error: ${it.message}")
            }
    }

    private fun fetchStandardGroupImageFromFirebaseDatabase() {
        FirebaseStorage.getInstance().getReference("/images/standard_group_image.jpg").downloadUrl.addOnSuccessListener {
            standardGroupImageUrl = it.toString()
        }
            .addOnFailureListener {
                Log.d(tagNewGroup, "Error: ${it.message}")
            }
    }

    private fun setupMenuBar(){
        supportActionBar?.title = "Create Group"
        supportActionBar?.setDisplayShowTitleEnabled(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            try {
                selectedPhotoUri = data?.data
                val bitmap = when {
                    Build.VERSION.SDK_INT > 28 -> {
                        val source = ImageDecoder.createSource(this.contentResolver, selectedPhotoUri!!)
                        ImageDecoder.decodeBitmap(source)
                    }
                    else -> {
                        MediaStore.Images.Media.getBitmap(this.contentResolver, selectedPhotoUri)
                    }
                }
                activityBinding.groupImageNewGroupCircleview.setImageBitmap(bitmap)
                activityBinding.selectPhotoNewGroupButton.alpha = 0f

            }catch (e:Throwable){
                Log.d(tagNewGroup,"$e")
            }
        }
    }
}