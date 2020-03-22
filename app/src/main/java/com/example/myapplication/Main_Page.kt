package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class Main_Page : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swiping)
    }

    fun toSettings(view: View){
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    fun toSetLocation(view: View){
        val intent = Intent(this, SetLocationActivity::class.java)
        startActivity(intent)
    }

    fun toShortlisted(view: View){
        val intent = Intent(this, ShortlistedRestaurantsActivity::class.java)
        startActivity(intent)
    }
    //database call
    val database = Firebase.database
    val myRef = database.getReference("message")
    fun toDetails(view: View){
        val intent = Intent(this, ScrollingActivity::class.java)
        startActivity(intent)
    }

    fun databaseTest(view: View){
        // Write a message to the database
        myRef.setValue("Message test 2")
    }

    fun databaseRead(view: View){
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue()
                println(value)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })
    }



//    override fun onOptionsItemSelected(item: MenuItem?) = when(item?.itemId){
//        R.id.SettingsButton -> {
//            print("button clicked")
//            toSettings()
//            true
//        }else -> {
//            super.onOptionsItemSelected(item)
//        }
//    }


}
