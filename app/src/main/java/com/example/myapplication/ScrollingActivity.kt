package com.example.myapplication

import android.os.Bundle
import android.view.MenuItem
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class ScrollingActivity : AppCompatActivity() {

    lateinit var place_id : String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        var restaurant = intent.getSerializableExtra("restaurant_to_pass") as Restaurant
        place_id = restaurant.place_id



        setContentView(R.layout.activity_scrolling)


        val r_name = findViewById<TextView>(R.id.RestaurantName)
        r_name.text=restaurant.name
        val r_address = findViewById<TextView>(R.id.RestaurantAddress)
        r_address.text=restaurant.address

        val r_rating = findViewById<RatingBar>(R.id.RestaurantRating)
        r_rating.rating =restaurant.rating.toFloat()
        val r_opening = findViewById<TextView>(R.id.RestaurantOpeningHours)
        if(restaurant.opening_now)
        {
            r_opening.text="Opening Now"
        }
        else
        {
            r_opening.text="Closes"
        }
        val r_pricing = findViewById<TextView>(R.id.RestaurantPricing)
        if(restaurant.price_level==-1)
        {
            r_pricing.text="Price: Not Applicable"
        }
        else
        {
            r_pricing.text="$".repeat(restaurant.price_level)
        }

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
