package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

private val PHOTO_STRS = listOf("https://media-cdn.tripadvisor.com/media/photo-s/0f/b2/5f/35/this-is-what-kfc-is-famous.jpg", "https://imgix.bustle.com/uploads/image/2019/4/9/e5e17083-273e-40f5-91cf-63a5ca339e99-ea3557c8-71a1-48e8-967f-4c166054baab-pizza-image_no-text.jpg?w=1020&h=574&fit=crop&crop=faces&auto=format&q=70")


class FinalActivity : AppCompatActivity() {

    private lateinit var adapter: PhotoListAdapter
    private lateinit var photoPager: ViewPager2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        //setSupportActionBar(findViewById(R.id.FinalToolbar))
        adapter = PhotoListAdapter()
        adapter.setItemList(PHOTO_STRS)
        photoPager = findViewById(R.id.final_photo_pager)
        photoPager.adapter = adapter

//        val requestQueue = SingletonObjects.getInstance(this).requestQueue
//        requestQueue.add(APIHelper.placeDetailsRequest(place_id
//        ) {
//            adapter.setItemList(it.toList())
//        })

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
