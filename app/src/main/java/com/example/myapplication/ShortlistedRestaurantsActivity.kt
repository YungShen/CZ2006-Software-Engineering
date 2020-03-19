package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView

class ShortlistedRestaurantsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shortlistedrestaurants)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun toFinalActivity(view: View) {
//        val restaurant = findViewById<TextView>(R.id.ShortlistedRestaurantName)
        val intent = Intent(this, FinalActivity::class.java)
        startActivity(intent)
    }

    private fun toHistory(){
        val intent = Intent(this, History::class.java)
        startActivity(intent)
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


    // recycler view adapter
    // recycler view viewholder
    // setListener on recyclerview item?

    // delete one shortlisted restaurant
    // random pick & startfinalactivity
}
