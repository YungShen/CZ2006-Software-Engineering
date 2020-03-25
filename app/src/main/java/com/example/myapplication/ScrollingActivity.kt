package com.example.myapplication

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ScrollingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    //database call
    val database = Firebase.database
    val databasePlace = database.getReference("Places")

    //start listener

//    val voteListener = object : ValueEventListener {
//        override fun onDataChange(dataSnapshot: DataSnapshot) {
//            // Get Photo object and use the values to update the UI
//            val photo = dataSnapshot.getValue<Photo>()
//            // ...
//        }
//    }


    fun upVote(view: View){
        val original = 14 //retrieve original vote based on photo
        val photo = Photo("testPhotoId", original+1)
        val placeId = "1221" //link place id to maps
        databasePlace.child(placeId).setValue(photo)
    }

    fun downVote(view: View){
        val original = 5 //retrieve original vote based on photo
        val photo = Photo("testPhotoId", original-1)
        val placeId = "1221" //link place id to maps
        databasePlace.child(placeId).setValue(photo)
    }

    // get more review?
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
