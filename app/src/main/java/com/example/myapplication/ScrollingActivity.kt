package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity


class ScrollingActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        var restaurant = getIntent().getSerializableExtra("restaurant_to_pass")
        Log.d("Passdata", "Obeject received $restaurant")
        setContentView(R.layout.activity_scrolling)
        actionBar?.setDisplayHomeAsUpEnabled(true)

    }

    // vote photo
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
