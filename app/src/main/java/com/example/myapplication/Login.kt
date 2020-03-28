package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val login = findViewById<Button>(R.id.LoginButton)
        login.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, Main_Page::class.java)
            finish()
            startActivity(intent)
        })
    }

    fun authenticateUser(view: View){
        //some checks
        toMainPage(view)
    }

    // to fix: there is
    private fun toMainPage(view: View){
        val intent = Intent(this, Main_Page::class.java)
        finish()
        startActivity(intent)
    }

    fun toRegister(view: View){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    fun toForgetPassword(view: View){
        val intent = Intent(this, ForgetPasswordActivity::class.java)
        startActivity(intent)
    }
}
