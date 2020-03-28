package com.example.myapplication

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class SingletonObjects constructor(context: Context){
    companion object {
        @Volatile
        private var INSTANCE: SingletonObjects? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: SingletonObjects(context).also {
                    INSTANCE = it
                }
            }
    }
    val requestQueue : RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }
}

class APIHelper {

    companion object{

        private val API_KEY = "AIzaSyATXIXpRO7l62cUt_vhiSzdOeSiiwKEnSU"
        private val radius = 1500
        private val latitude = -33.8670522
        private val longitude = 151.1957362

        fun nearbyPlacesRequest(nearbyRestaurants: MutableList<Restaurant>, callback: ()->Unit): JsonObjectRequest {
            val url =
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$latitude,$longitude&radius=$radius&type=restaurant&key=$API_KEY"
            val request = JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener<JSONObject?>() {
                        response -> if(response != null){

                    val results = response.getJSONArray("results")
                    for(i in 0..(results.length()-1)){
                        val restaurant = results.getJSONObject(i)
                        val name = restaurant.getString("name")
                        val place_id = restaurant.getString("place_id")
                        val address = restaurant.getString("vicinity")
                        val location = restaurant.getJSONObject("geometry").getJSONObject("location")
                        val latitude = location.getDouble("lat")
                        val longitude = location.getDouble("lng")

                        var price_level : Int = -1
                        var rating : Double = -1.0
                        var user_ratings_total : Int = -1

                        var opening_now : Boolean = true
                        if(restaurant.has("price_level")){
                            price_level = restaurant.getInt("price_level")
                        }
                        if(restaurant.has("rating")){
                            rating = restaurant.getDouble("rating")
                        }
                        if(restaurant.has("user_ratings_total")){
                            user_ratings_total = restaurant.getInt("user_ratings_total")
                        }
                        if(restaurant.has("opening_now")){
                            opening_now = restaurant.getBoolean("opening_now")
                        }

                        val photos = restaurant.getJSONArray("photos")
                        val url : String
                        if(photos.length() != 0) {
                            val reference = photos.getJSONObject(0).getString("photo_reference")
                            url = getPhotoUrl(reference)
                        }else{
                            url = "https://www.nicepng.com/png/detail/214-2148603_you-eat-ready-to-eat-food-icon.png"
                        }

                        nearbyRestaurants.add(
                            Restaurant(name = name, place_id = place_id, address = address,
                            price_level = price_level, rating = rating, user_ratings_total = user_ratings_total,
                            opening_now = opening_now, url = url, latitude = latitude, longitude = longitude)
                        )
                        Log.d("Json Init", "$name\n$place_id\n$address\n$price_level\n$rating\n$opening_now\n$url\n$latitude, $longitude")
                    }
                    callback()

                }
                }, Response.ErrorListener { error -> error.printStackTrace() })
            return request
        }

        private fun getPhotoUrl(photo_reference : String) : String{
            val maxWidth = 400
            val maxHeight = 400
            var url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=$maxWidth&maxHeight=$maxHeight&photoreference=$photo_reference&key=$API_KEY"
            return url
        }
    }


}