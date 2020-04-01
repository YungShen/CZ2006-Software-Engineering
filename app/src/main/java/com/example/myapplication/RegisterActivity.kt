package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    companion object {
        const val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        RegisterButton.setOnClickListener{
            performRegister(it)
        }

    }


    private fun performRegister(view: View){
        val email = Email.text.toString()
        val password = Password.text.toString()
        val retypedPassword = RetypePassword.text.toString()

        if (email.isEmpty()|| password.isEmpty()|| retypedPassword.isEmpty()){
            Toast.makeText(this, "Please fill up all fields!", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != retypedPassword){
            Toast.makeText(this, "Passwords are not equal!", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "Attempting to create user with email: $email")

        // Firebase Authentication to create a user with email and password
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                it.result?.user?.uid?.let { it1 -> writeNewUser(it1) }

                // else if successful
                Log.d(TAG, "Successfully created user with uid: ${it.result?.user?.uid}")

                //send verification email
                auth.currentUser?.sendEmailVerification()
                    ?.addOnCompleteListener {
                        // verification email sent successfully
                        if (it.isSuccessful){
                            Toast.makeText(this, "Registered successfully! Please check your email for verification.",Toast.LENGTH_SHORT).show()
                            // clear all fields
                            Email.setText("")
                            Password.setText("")
                            RetypePassword.setText("")
                        }
                        // verification email not sent
                    }?.addOnFailureListener {
                        Toast.makeText(this, "Failed to send verification email: ${it.message}", Toast.LENGTH_SHORT).show()
                    }

                //to Success Page
                toLogin()
            }
            .addOnFailureListener{
                //Log.d(TAG, "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    class User(val uid: String, val username: String)

    //create new User settings
    private val database = Firebase.database
    private val databaseNewUser = database.getReference("Users")

    private fun writeNewUser(userId: String) {
        val userSettings = UserSettings(userId, false, false, 1)
        databaseNewUser.child(userId).setValue(userSettings)
    }

    // validate input
    fun validateUser(view: View){
        // some checks
        displaySuccess()
    }

    private fun displaySuccess() {
        val intent = Intent(this, ResetLinkActivity::class.java)
        finish()
        startActivity(intent)
    }

    private fun toLogin() {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }
}
