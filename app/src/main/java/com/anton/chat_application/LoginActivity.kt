package com.anton.chat_application

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private val tagLogin = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupMenuBar()

        login_Login_button.setOnClickListener {
            performLogin()
        }

        returnToSignup_Login_textView.setOnClickListener {
            Log.d(tagLogin, "Try to show register activity")

            finish()
        }
    }

    private fun performLogin() {
        val email = email_Login_editText.text.toString()
        val password = password_Login_editTextPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter your account information", Toast.LENGTH_LONG).show()
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
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun setupMenuBar(){
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setLogo(R.drawable.logo)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }
}