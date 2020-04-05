package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.LoginButton

class LoginActivity : AppCompatActivity() {

    companion object {
        const val TAG = "Login"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        LoginButton.setOnClickListener {
            performLogin()
        }
        SignUpButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        ForgetPasswordText.setOnClickListener {
            val intent = Intent(this, ForgetPasswordActivity::class.java)
            startActivity(intent)
        }

    }

    private fun performLogin() {
        val email = loginEmail.text.toString()
        val password = loginPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill up all fields!", Toast.LENGTH_SHORT).show()
            return
        }

        DatabaseHelper.signIn(email, password, {successCallback()}, {failureCallback(it)})
    }

    private fun failureCallback(error : String){
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    private fun successCallback(){
        val intent = Intent(this, MapsActivity::class.java)
        finish()
        startActivity(intent)
    }
}
