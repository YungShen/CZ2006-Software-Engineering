package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem

class FinalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final)
        setSupportActionBar(findViewById(R.id.FinalToolbar))
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.ShareToFacebookButton -> {
            // Redirect to facebook
            true
        }

        R.id.ShareToWhatsappButton -> {
            // Redirect to whatsapp
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

}
