package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.random.Random

class ShortlistedRestaurantsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ShortlistedRestaurantAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var shortlistedrestaurants = intent.getSerializableExtra("restaurant_list_to_pass")as ArrayList<Restaurant>
        setContentView(R.layout.activity_shortlistedrestaurants)

        actionBar?.setDisplayHomeAsUpEnabled(true)
        viewManager = LinearLayoutManager(this)
        viewAdapter = ShortlistedRestaurantAdapter(shortlistedrestaurants)
        recyclerView = findViewById<RecyclerView>(R.id.ShortlistedRestaurants).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter

        }

        val clearAll = findViewById<Button>(R.id.ClearAllButton)
        clearAll.setOnClickListener {
            viewAdapter.clearAllRestaurant()
        }

        val randomPick = findViewById<Button>(R.id.RandomPickButton)
        randomPick.setOnClickListener {
            val randomInt = Random.nextInt(shortlistedrestaurants.size)
            val intent = Intent(this, FinalActivity::class.java)
                .putExtra("restaurant_to_final", viewAdapter.getRestaurants()[randomInt])
            startActivity(intent)
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

}
