package com.example.myapplication

import com.google.android.gms.maps.model.LatLng


object mySettings {

    var uid: String = ""
    var halal: Boolean = false
    var vegetarian: Boolean = false
    var radius: Int = 1
    lateinit var locationOfUser: LatLng
    lateinit var locationOfRestaurant: LatLng
}