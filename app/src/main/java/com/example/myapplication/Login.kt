package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.LoginButton
import kotlinx.android.synthetic.main.activity_register.*

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var currentSettings: UserSettings
    private lateinit var ref: DatabaseReference

    companion object {
        const val TAG = "Login"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()


        LoginButton.setOnClickListener {
            performLogin(it)
        }
        SignUpButton.setOnClickListener {
            toRegister(it)
        }

        forgetPassword.setOnClickListener {
            toForgetPassword(it)
        }

    }

//    fun authenticateUser(view: View){
//        //some checks
//        toMainPage(view)
//    }

    // to fix: there is
    private fun toMainPage(view: View){
        val intent = Intent(this, Main_Page::class.java)
        finish()
        startActivity(intent)
    }

    private fun toRegister(view: View){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun toForgetPassword(view: View){
        val intent = Intent(this, ForgetPasswordActivity::class.java)
        startActivity(intent)
    }

    private fun performLogin(view: View) {
        val email = loginEmail.text.toString()
        val password = loginPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill up all fields!", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                if (!auth.currentUser?.isEmailVerified!!){
                    Toast.makeText(this, "Please verify your email address!", Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }
                //retrieve user settings
                ref = FirebaseDatabase.getInstance().getReference("Users")

                //retrieve settings listener
                ref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        Log.w(TAG, "stage 1")
                        if(dataSnapshot!!.exists()) {
                            Log.w(TAG, "stage 2")
                            for (u in dataSnapshot.children){
                                Log.w(TAG, "stage $u")
                                if (u.key == it.result?.user?.uid) {
                                    Log.w(TAG, "stage 4")
                                    currentSettings = u.getValue(UserSettings::class.java)!!
                                    mySettings.radius = currentSettings.radius
                                    mySettings.halal = currentSettings.halal
                                    mySettings.uid = currentSettings.uid
                                    mySettings.vegetarian = currentSettings.vegetarian
                                    Log.w(TAG, "user settings retrieved")

                                    Log.w(TAG, "halal ${currentSettings.halal}")
                                    Log.w(TAG, "veg ${currentSettings.vegetarian}")
                                    Log.w(TAG, "radius ${currentSettings.radius}")
                                }
                            }

                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Getting Post failed, log a message
                        Log.w(TAG, "settings:onCancelled", databaseError.toException())
                        // ...
                    }
                })

                toMainPage(view)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to log in: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
