package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }

    // validate input
    fun validateUser(view: View){
        // some checks
        displaySuccess(view)
    }

    private fun displaySuccess(view: View){
        val intent = Intent(this, ResetLinkActivity::class.java)
        finish()
        startActivity(intent)
    }

    fun toLogin(view: View){
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }
}
