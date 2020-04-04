package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_final.*
import kotlinx.android.synthetic.main.restaurant_text.*

class FinalActivity : AppCompatActivity() {

    private lateinit var adapter: PhotoListAdapter
    private lateinit var photoPager: ViewPager2
    private var web = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val restaurant = intent.getSerializableExtra("restaurant_to_final") as Restaurant

        setContentView(R.layout.activity_final)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        adapter = PhotoListAdapter()
        photoPager = findViewById(R.id.final_photo_pager)
        photoPager.adapter = adapter

        val requestQueue = SingletonObjects.getInstance(this).requestQueue
        requestQueue.add(APIHelper.placeDetailsPhotosRequest(restaurant.place_id)
        {photoRefs ->
            // the returned list is a bunch of photoReferences, not urls
            if (photoRefs.size < PhotoList.MAX_PHOTO) {
                setPhotoRefsToAdapter(photoRefs)
            } else {
                photoRefs.shuffle()
                // dynamically insert to the adapter if the vote is not negative
                photoRefs.forEach { item ->
                    DatabaseHelper.getVote(
                        restaurant.place_id,item) { numVote, photoRef -> addToAdapterCallback(numVote, photoRef) }
                }
            }
        })
        requestQueue.add(APIHelper.placeDetailsOthersRequest(restaurant.place_id) { phone: String, web: String ->
            val phoneView = findViewById<TextView>(R.id.PhoneNumber)
            phoneView.text = phone
            val websiteView = findViewById<TextView>(R.id.RestaurantWebsite)
            websiteView.text = web
            // for reservation
            this.web = web
        })

        setupRestaurantText(restaurant)

        DirectionButton.setOnClickListener {
//            val intent =
//                Intent(this, GetDirectionsActivity::class.java)
//            intent.putExtra("latitude", restaurant.latitude)
//            intent.putExtra("longitude", restaurant.longitude)
//            startActivity(intent)
            val temp:LatLng =  LatLng(restaurant.latitude,restaurant.longitude)
            mySettings.locationOfRestaurant = temp
            val destinationURL =
                "http://maps.google.com/maps?saddr=" + mySettings.locationOfUser.latitude + "," + mySettings.locationOfUser.longitude + "&daddr=" + mySettings.locationOfRestaurant.latitude + "," + mySettings.locationOfRestaurant.longitude
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(destinationURL)
            )
            startActivity(intent)
        }

        ReservationButton.setOnClickListener {
            if(web.isNotBlank() && web.isNotEmpty()){
                val intent =
                    Intent(Intent.ACTION_VIEW,Uri.parse(web as String?))
                startActivity(intent)
            }else{
                Toast.makeText(this@FinalActivity,"No website available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addToAdapterCallback(numVote: Int, photoRef: String){
        if(numVote >= 0 && adapter.itemCount < PhotoList.MAX_PHOTO){
            adapter.addItemToList(APIHelper.getPhotoUrl(photoRef))
        }
    }

    private fun setPhotoRefsToAdapter(photoRefs : MutableList<String>){
        for(i in 0 until photoRefs.size){
            photoRefs[i] = APIHelper.getPhotoUrl(photoRefs[i])
        }
        adapter.setItemList(photoRefs.toList())
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
