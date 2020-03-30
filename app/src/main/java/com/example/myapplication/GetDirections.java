package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.model.LatLng;

public class GetDirections extends AppCompatActivity {

    private String destinationURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_directions);
        getDirections();
    }

    public void getDirections() {
        destinationURL = "http://maps.google.com/maps?saddr=" + mySettings.locationOfUser.latitude + "," + mySettings.locationOfUser.longitude + "&daddr=1.337773,103.6951003";
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(destinationURL));
        startActivity(intent);
    }


}
