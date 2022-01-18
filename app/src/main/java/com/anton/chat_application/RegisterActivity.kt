package com.anton.chat_application

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.anton.chat_application.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import id.zelory.compressor.Compressor
import id.zelory.compressor.Compressor.compress
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private val tagRegister = "RegisterActivity"
    private var selectedPhotoUri: Uri? = null
    private var imageDownloadUrl: String = ""
    private var username: String = ""
    private var standardProfileImageUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setupMenuBar()
        fetchStandardProfileImageFromFirebaseDatabase()

        selectPhoto_Register_button.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        register_Register_button.setOnClickListener {
            performRegister()
        }

        alreadyHaveAccount_Register_textView.setOnClickListener {
            Log.d(tagRegister, "Try to show login activity")

            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    //TODO: Kolla så att username är unikt
    private fun performRegister() {
        val email = email_Register_editText.text.toString()
        val password = password_Register_editTextPassword.text.toString()
        username = username_Register_editText.text.toString()

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Please enter your account information", Toast.LENGTH_LONG).show()
            return
        }

        Log.d(tagRegister, "Username is: $username")
        Log.d(tagRegister, "Email is: $email")
        Log.d(tagRegister, "Password is: $password")

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                Log.d(tagRegister, "Successfully created user with UID: ${it.result.user?.uid}")

                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener {
                Log.d(tagRegister, "Failed to create user: ${it.message}")
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) {
            saveUserToFirebaseDatabase()
            return
        }

        Log.d(tagRegister, "Starting upload")
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/profilePhotos/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener { it ->
                Log.d(tagRegister, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(tagRegister, "File location: $it")
                    imageDownloadUrl = it.toString()
                    saveUserToFirebaseDatabase()
                }
                    .addOnFailureListener {
                        Log.d(tagRegister, "Error: ${it.message}")
                    }
            }
            .addOnFailureListener {
                Log.d(tagRegister, "Error: ${it.message}")
            }
    }

    private fun saveUserToFirebaseDatabase() {
        val uid = FirebaseAuth.getInstance().uid.toString()
        val ref = FirebaseDatabase.getInstance("https://harmony-chatapp-default-rtdb.europe-west1.firebasedatabase.app").getReference("/users/$uid")
        val imageUrl = when (imageDownloadUrl) {
            ""   -> standardProfileImageUrl
            else -> imageDownloadUrl
        }
        Log.d(tagRegister, "ImageURL: $imageUrl")
        val user = Models.User(uid, username, imageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(tagRegister, "Saved user to Firebase Database")

                val intent = Intent(this, GroupsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d(tagRegister, "Error: ${it.message}")
            }
    }

    private fun fetchStandardProfileImageFromFirebaseDatabase() {
        FirebaseStorage.getInstance().getReference("/images/standard_profile_image.png").downloadUrl.addOnSuccessListener {
            standardProfileImageUrl = it.toString()
        }
            .addOnFailureListener {
                Log.d(tagRegister, "Error: ${it.message}")
            }
    }

    private fun setupMenuBar(){
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setLogo(R.drawable.logo)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
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
                profileImage_Register_circleview.setImageBitmap(bitmap)
                selectPhoto_Register_button.alpha = 0f

            }catch (e:Throwable){
                Log.d(tagRegister,"$e")
            }
        }
    }
}