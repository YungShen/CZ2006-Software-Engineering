package com.example.myapplication

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.restaurant_text.*


class ScrollingActivity : AppCompatActivity() {

    lateinit var place_id : String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val restaurant = intent.getSerializableExtra("restaurant_to_pass") as Restaurant
        place_id = restaurant.place_id

        setContentView(R.layout.activity_scrolling)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val requestQueue = SingletonObjects.getInstance(this).requestQueue
        requestQueue.add(APIHelper.placeDetailsOthersRequest(restaurant.place_id) { phone: String, web: String ->
            PhoneNumber.text = phone
            RestaurantWebsite.text = web
        })

        setupRestaurantText(restaurant)
    }

    private fun setupRestaurantText(restaurant: Restaurant){
        RestaurantName.text=restaurant.name
        RestaurantAddress.text=restaurant.address
        RestaurantRating.rating =restaurant.rating.toFloat()
        if(restaurant.opening_now) {
            RestaurantOpeningHours.text="Opening Now"
        } else {
            RestaurantOpeningHours.text="Closes"
        }
        if(restaurant.price_level==-1) {
            RestaurantPricing.text="Price Not Applicable"
        } else {
            RestaurantPricing.text="$".repeat(restaurant.price_level)
        }
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
