package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SettingsActivity : AppCompatActivity() {

    //var loginInfo = Login()
    private val MIN_PROGRESS = 1

    private var halal = mySettings.halal  //loginInfo.currentSettings.halal
    private var veg = mySettings.vegetarian //loginInfo.currentSettings.vegetarian
    private var distance = 1000 * mySettings.radius //loginInfo.currentSettings.radius
    private val halalCopy = halal
    private val vegCopy = veg
    private val distanceCopy = distance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_page_2)
        val distance_value = findViewById(R.id.DistanceValue) as TextView
        distance_value.text=distance.toString()
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val halalSwitch = findViewById<Switch>(R.id.HalalSwitch)
        halalSwitch.isChecked = halal
        val vegSwitch = findViewById<Switch>(R.id.VegetarianSwitch)
        vegSwitch.isChecked = veg
        halalSwitch.setOnCheckedChangeListener {
                _, isChecked -> halal = isChecked
        }
        vegSwitch.setOnCheckedChangeListener {
                _, isChecked -> veg = isChecked
        }


        val seek = findViewById<SeekBar>(R.id.DistanceSelector)
        seek.progress = mySettings.radius
        seek?.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            val radiusValue = findViewById<TextView>(R.id.DistanceValue)
            override fun onProgressChanged(seek: SeekBar?, progress: Int, fromUser: Boolean) {
                if(progress < MIN_PROGRESS){
                    seek?.progress = MIN_PROGRESS
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
            View.OnClickListener {
                saveSettings()
                var settingsChanged = false
                if(halal != halalCopy || veg != vegCopy || distance != distanceCopy){
                    settingsChanged = true
                }
                Log.d("SettingsActivity.kt", "settingsChanged passing to Main_Page: $settingsChanged")
                setResult(Activity.RESULT_OK, Intent().putExtra("settingsChanged", settingsChanged))
                finish()
            }
        )

    }


    fun saveSettings(){
        // save
        val database = Firebase.database
        val databaseUsers = database.getReference("Users")

        databaseUsers.child(mySettings.uid).setValue(UserSettings(mySettings.uid, halal, veg, distance/1000))
        mySettings.halal = halal
        mySettings.vegetarian = veg
        mySettings.radius = distance/1000


        
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
