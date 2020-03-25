package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun authenticateUser(view: View){
        //some checks

        //if authentication successful
        //retrieve user settings

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
