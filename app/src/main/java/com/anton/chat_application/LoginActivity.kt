package com.anton.chat_application

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.anton.chat_application.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {
    private val tagLogin = "LoginActivity"
    private lateinit var activityBinding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityLoginBinding.inflate(layoutInflater)
        val view = activityBinding.root
        setContentView(view)

        setupMenuBar()

        activityBinding.loginLoginButton.setOnClickListener {
            performLogin()
        }

        activityBinding.returnToSignupLoginTextView.setOnClickListener {
            Log.d(tagLogin, "Try to show register activity")

            finish()
        }
    }

    private fun performLogin() {
        val email = activityBinding.emailLoginEditText.text.toString()
        val password = activityBinding.passwordLoginEditTextPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            GeneralFunctions().generateSnackBar(this, activityBinding.root, "Please enter your account information")
            return
        }

        Log.d(tagLogin, "Login with email: $email")
        Log.d(tagLogin, "Login with password: $password")

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                Log.d(tagLogin, "Successfully logged in user with UID: ${it.result.user?.uid}")

                val intent = Intent(this, GroupsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d(tagLogin, "Failed to login user: ${it.message}")
                GeneralFunctions().generateSnackBar(this, activityBinding.root, "Error: ${it.message}")
            }
    }

    private fun setupMenuBar(){
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setLogo(R.drawable.logo)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }
}