package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class GetDirectionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_directions);

        double lat = getIntent().getDoubleExtra("latitude", 1.337773);
        double lng = getIntent().getDoubleExtra("longitude", 103.6951003);

        String destinationURL = "http://maps.google.com/maps?saddr=" + mySettings.locationOfUser.latitude + "," + mySettings.locationOfUser.longitude + "&daddr="+lat+","+lng;
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(destinationURL));
        startActivity(intent);
    }

}
