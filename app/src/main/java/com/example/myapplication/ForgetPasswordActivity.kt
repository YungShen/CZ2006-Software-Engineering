package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgetpassword.*

class ForgetPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgetpassword)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        ResetButton.setOnClickListener{
            performReset(it)
        }

        LoginButton.setOnClickListener{
            toLogin(it)
        }
    }

    private fun performReset(view: View) {
        val email = Email.text.toString()

        if (email.isEmpty()){
            Toast.makeText(this, "Please enter registered email!", Toast.LENGTH_SHORT).show()
            return
        }

        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(this, "Password sent to your email", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to send password to email: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun toLogin(view: View){
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }
}
