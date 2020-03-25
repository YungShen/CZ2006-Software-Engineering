package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }

    //database call
    val database = Firebase.database
    val databaseNewUser = database.getReference("Users")

    // validate input
    fun validateUser(view: View){
        // some checks

        //if checks successful, add new acc info
        var editTextEmail = Email.text.toString()
        val userSettings = UserSettings(editTextEmail, false, false, 4)
        val key = databaseNewUser.push().key
        databaseNewUser.child(key.toString()).setValue(userSettings)
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


//    //database call
//    val database = Firebase.database
//    val databaseNewUser = database.getReference("Users")
//
//    fun addAccount(view: View){
//        var editTextEmail = Email.text.toString()
//        val userSettings = UserSettings(editTextEmail, false, false, 4, Photo("ccc", 5))
//        val key = databaseNewUser.push().key
//        databaseNewUser.child(key.toString()).setValue(userSettings)
//    }



}
