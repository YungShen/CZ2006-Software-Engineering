package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class FinalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        //setSupportActionBar(findViewById(R.id.FinalToolbar))
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

    // Action to be performed when "Directions" is clicked
    fun sendMessage(view: View?) {
        val activityChangeIntent =
            Intent(this@FinalActivity, GetDirections::class.java)
        this@FinalActivity.startActivity(activityChangeIntent)
    }

}
