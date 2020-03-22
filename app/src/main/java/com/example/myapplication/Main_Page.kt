package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout

class Main_Page : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swiping)
    }

    fun toSettings(view: View){
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    fun toSetLocation(view: View){
        val intent = Intent(this, SetLocationActivity::class.java)
        startActivity(intent)
    }

    fun toShortlisted(view: View){
        val intent = Intent(this, ShortlistedRestaurantsActivity::class.java)
        startActivity(intent)
    }

    fun toDetails(view: View){
        val intent = Intent(this, ScrollingActivity::class.java)
        startActivity(intent)
    }

//    override fun onOptionsItemSelected(item: MenuItem?) = when(item?.itemId){
//        R.id.SettingsButton -> {
//            print("button clicked")
//            toSettings()
//            true
//        }else -> {
//            super.onOptionsItemSelected(item)
//        }
//    }

}
