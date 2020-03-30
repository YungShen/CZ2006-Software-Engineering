package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2




class FinalActivity : AppCompatActivity() {

    private lateinit var adapter: PhotoListAdapter
    private lateinit var photoPager: ViewPager2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var restaurant = getIntent().getSerializableExtra("restaurant_to_final") as Restaurant

        setContentView(R.layout.activity_final)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        //setSupportActionBar(findViewById(R.id.FinalToolbar))
        adapter = PhotoListAdapter()
        photoPager = findViewById(R.id.final_photo_pager)
        photoPager.adapter = adapter

        val requestQueue = SingletonObjects.getInstance(this).requestQueue
        requestQueue.add(APIHelper.placeDetailsRequest(restaurant.place_id) {
            for(i in 0 until it.size){
                it[i] = APIHelper.getPhotoUrl(it[i])
            }
            adapter.setItemList(it.toList())
        })


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
        if(restaurant.price_level==-1)
        {
            r_pricing.text="Not Applicable"
        }
        else
        {
            r_pricing.text="$".repeat(restaurant.price_level)
        }

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
