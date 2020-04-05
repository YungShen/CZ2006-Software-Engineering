package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_forgetpassword.*

class ForgetPasswordActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgetpassword)

        ResetButton.setOnClickListener{
            performReset()
        }

        LoginButton.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performReset() {
        val email = Email.text.toString()

        if (email.isEmpty()){
            Toast.makeText(this, "Please enter registered email!", Toast.LENGTH_SHORT).show()
            return
        }

        DatabaseHelper.passwordReset(email, { successCallback() }, { failureCallback(it) })

    }

    private fun successCallback(){
        Toast.makeText(this@ForgetPasswordActivity, "Reset password link had sent to your email", Toast.LENGTH_SHORT).show()
    }

    private fun failureCallback(exception : Exception){
        Toast.makeText(this@ForgetPasswordActivity, "Failed to send password to email: ${exception.message}", Toast.LENGTH_SHORT).show()
    }

}
