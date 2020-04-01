package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class FinalActivity : AppCompatActivity() {

    private lateinit var adapter: PhotoListAdapter
    private lateinit var photoPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var restaurant = intent.getSerializableExtra("restaurant_to_final") as Restaurant

        setContentView(R.layout.activity_final)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        adapter = PhotoListAdapter()
        photoPager = findViewById(R.id.final_photo_pager)
        photoPager.adapter = adapter

        val requestQueue = SingletonObjects.getInstance(this).requestQueue
        requestQueue.add(APIHelper.placeDetailsPhotosRequest(restaurant.place_id) {
            for(i in 0 until it.size){
                it[i] = APIHelper.getPhotoUrl(it[i])
            }
            adapter.setItemList(it.toList())
        })
        requestQueue.add(APIHelper.placeDetailsOthersRequest(restaurant.place_id) { phone: String, web: String ->
            val direction_button= findViewById<Button>(R.id.ReservationButton)
            direction_button.setOnClickListener {
                if(web != ""){
                    makeReservation(web)
                }else{
                    Toast.makeText(this@FinalActivity,"No website available", Toast.LENGTH_SHORT).show()
                }
            }
            val phoneView = findViewById<TextView>(R.id.PhoneNumber)
            phoneView.text = phone
            val websiteView = findViewById<TextView>(R.id.RestaurantWebsite)
            websiteView.text = web
        })


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
    fun sendMessage(view: View) {
        val activityChangeIntent =
            Intent(this, GetDirections::class.java)
        this@FinalActivity.startActivity(activityChangeIntent)
    }
    fun makeReservation(website:String) {

        val browserIntent =
            Intent(Intent.ACTION_VIEW,Uri.parse(website))
        this@FinalActivity.startActivity(browserIntent)
    }


}
