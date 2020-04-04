package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    companion object {
        const val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        RegisterButton.setOnClickListener{
            performRegister()
        }
        LoginButton.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performRegister(){
        val email = Email.text.toString()
        val password = Password.text.toString()
        val retypedPassword = RetypePassword.text.toString()

        val error = firstCheck(email, password, retypedPassword)
        if(error != ""){
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "Attempting to create user with email: $email")

        // Firebase Authentication to create a user with email and password
        DatabaseHelper.register(email, password, {successCallback()}, {failureCallback(it)})
    }

    private fun firstCheck(email : String, password : String, retypedPassword : String) : String{
        return if (email.isEmpty()|| password.isEmpty()|| retypedPassword.isEmpty()){
            "Please fill up all fields!"
        }else if (password != retypedPassword){
            "Passwords are not equal!"
        }else{
            ""
        }
    }

    private fun successCallback(){
        Toast.makeText(this, "Registered successfully! Please check your email for verification.",Toast.LENGTH_SHORT).show()
        // clear all fields
        Email.setText("")
        Password.setText("")
        RetypePassword.setText("")
        // to success page
        displaySuccess()
    }

    private fun displaySuccess() {
        val intent = Intent(this, ResetLinkActivity::class.java)
        finish()
        startActivity(intent)
    }

    private fun failureCallback(error : String){
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

}
