package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


class ScrollingActivity : AppCompatActivity() {

    lateinit var place_id : String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        var restaurant = getIntent().getSerializableExtra("restaurant_to_pass") as Restaurant
        place_id = restaurant.place_id

        Log.d("Passed Object","$restaurant::class")

        setContentView(R.layout.activity_scrolling)


        val r_name = findViewById(R.id.RestaurantName) as TextView
        r_name.text=restaurant.name
        val r_address = findViewById(R.id.RestaurantAddress) as TextView
        r_address.text=restaurant.address

        val r_rating = findViewById(R.id.RestaurantRating)as RatingBar
        r_rating.rating =restaurant.rating.toFloat()
        val r_opening = findViewById(R.id.RestaurantOpeningHours) as TextView
        if(restaurant.opening_now==true )
        {
            r_opening.text="Opening Now"
        }
        else
        {
            r_opening.text="Closes"
        }
        val r_pricing = findViewById(R.id.RestaurantPricing) as TextView
        r_pricing.text="$"+restaurant.price_level.toString()
        actionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}
