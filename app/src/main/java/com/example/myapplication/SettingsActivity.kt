package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_settings.*

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
        setContentView(R.layout.activity_settings)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        HalalSwitch.isChecked = halal
        HalalSwitch.setOnCheckedChangeListener {
                _, isChecked -> halal = isChecked
        }
        VegetarianSwitch.isChecked = veg
        VegetarianSwitch.setOnCheckedChangeListener {
                _, isChecked -> veg = isChecked
        }
        DistanceValue.text=distance.toString()

        DistanceSelector.progress = mySettings.radius
        DistanceSelector.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
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

        LogoutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            finish()
            startActivity(intent)
        }

        CancelButton.setOnClickListener {
            onBackPressed()
        }

        SaveButton.setOnClickListener {

            DatabaseHelper.saveSettings(halal, veg, distance)
            var settingsChanged = false
            if(halal != halalCopy || veg != vegCopy || distance != distanceCopy){
                settingsChanged = true
            }
            setResult(Activity.RESULT_OK, Intent().putExtra("settingsChanged", settingsChanged))
            onBackPressed()
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
