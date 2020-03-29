package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ShortlistedRestaurantsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var mockRestaurants= mutableListOf<String>(
        "KFC",
        "MCD"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var shortlistedrestaurants = getIntent().getSerializableExtra("restaurant_list_to_pass")as ArrayList<Restaurant>
        setContentView(R.layout.activity_shortlistedrestaurants)

//        setSupportActionBar(findViewById(R.id.ShortlistedToolbar))
        actionBar?.setDisplayHomeAsUpEnabled(true)
        viewManager = LinearLayoutManager(this)
        viewAdapter = ShortlistedRestaurantAdapter(shortlistedrestaurants)
        recyclerView = findViewById<RecyclerView>(R.id.ShortlistedRestaurants).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter

        }
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
//        R.id.ViewHistory -> {
//            val intent = Intent(this, History::class.java)
//            startActivity(intent)
//            true
//        }
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
