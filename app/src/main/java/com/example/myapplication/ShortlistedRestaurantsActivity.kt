package com.example.myapplication

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_shortlistedrestaurants.*
import kotlin.random.Random

class ShortlistedRestaurantsActivity : AppCompatActivity() {
    private lateinit var viewAdapter: ShortlistedRestaurantAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shortlistedrestaurants)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val shortlistedRestaurants = intent.getSerializableExtra("restaurant_list_to_pass")as ArrayList<Restaurant>

        val viewManager = LinearLayoutManager(this)
        viewAdapter = ShortlistedRestaurantAdapter(shortlistedRestaurants)
        ShortlistedRestaurants.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        ClearAllButton.setOnClickListener {
            viewAdapter.clearAllRestaurant()
        }

        RandomPickButton.setOnClickListener {
            if(shortlistedRestaurants.size != 0){
                val randomInt = Random.nextInt(shortlistedRestaurants.size)
                val intent = Intent(this, FinalActivity::class.java)
                    .putExtra("restaurant_to_final", viewAdapter.getRestaurants()[randomInt])
                startActivity(intent)
            }else{
                Toast.makeText(this@ShortlistedRestaurantsActivity,"Your have not shortlisted any restaurants yet!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            setResult(Activity.RESULT_OK, Intent().putExtra("restaurant_list_to_pass_back", viewAdapter.getRestaurants()))
            onBackPressed()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

}
