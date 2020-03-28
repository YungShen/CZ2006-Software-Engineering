package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*

class SettingsActivity : AppCompatActivity() {

    private var halal = false
    private var veg = false
    private var distance = 1000
    private val minProgress = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_page_2)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val halalSwitch = findViewById<Switch>(R.id.HalalSwitch)
        val vegSwitch = findViewById<Switch>(R.id.VegetarianSwitch)
        halalSwitch.setOnCheckedChangeListener {
                _, isChecked -> halal = isChecked
        }
        vegSwitch.setOnCheckedChangeListener {
                _, isChecked -> veg = isChecked
        }

        val seek = findViewById<SeekBar>(R.id.DistanceSelector)
        seek?.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            val radiusValue = findViewById<TextView>(R.id.DistanceValue)
            override fun onProgressChanged(seek: SeekBar?, progress: Int, fromUser: Boolean) {
                if(progress < minProgress){
                    seek?.progress = minProgress
                }else{
                    distance = progress*1000
                    radiusValue.text = distance.toString()
                }
            }
            override fun onStartTrackingTouch(seek: SeekBar?) {}
            override fun onStopTrackingTouch(seek: SeekBar) {}
        })

        val logout = findViewById<Button>(R.id.LogoutButton)
        logout.setOnClickListener(
            View.OnClickListener {
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
            }
        )

        val save = findViewById<Button>(R.id.SaveButton)
        save.setOnClickListener(
            View.OnClickListener { saveSettings() }
        )

    }

    fun saveSettings(){
        // save
        Toast.makeText(this@SettingsActivity, "halal: $halal\nveg: $veg\nradius: $distance",
            Toast.LENGTH_SHORT).show()
        // back to main page
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
