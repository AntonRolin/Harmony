package com.anton.chat_application

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class GeneralFunctions {

    fun generateSnackBar(context: Context, view: View, message: String) {
        val imm: InputMethodManager = context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)

        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }
}