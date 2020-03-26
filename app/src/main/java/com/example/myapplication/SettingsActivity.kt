package com.example.myapplication

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference

class SettingsActivity : AppCompatActivity() {

    private val TAG = "SettingsActivity"
    private lateinit var mUser: UserSettings
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    fun DataSnapshot.asUser(): UserSettings? =
        getValue(UserSettings::class.java)?.copy(email = key)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_page_2)
        actionBar?.setDisplayHomeAsUpEnabled(true)

//        mAuth = FirebaseAuth.getInstance()
//        mDatabase = FirebaseDatabase.getInstance().reference
//
//        fun currentUserReference(): DatabaseReference =
//            mDatabase.child("Users").child(mAuth.currentUser!!.email)
//
//        currentUserReference().addListenerForSingleValueEvent(){
//            ValueListenerAdapter{
//                mUser = it.asUser()!!
//                loginEmail.setText(mUser.email)
//            }
//        }
    }

    fun saveSettings(view: View){
        // save
    }

    fun logout(view: View){
        // logout
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

}
