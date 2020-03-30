package com.example.myapplication

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import org.json.JSONArray
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
        private var radius = mySettings.radius*1000
        private var keyword = ""
        private var latitude = mySettings.locationOfUser.latitude
        private var longitude = mySettings.locationOfUser.longitude

        fun adjustToUserSettings(){
            radius = mySettings.radius*1000
            if(mySettings.halal && mySettings.vegetarian){
                keyword = "halal,vegetarian"
            }else if(mySettings.halal){
                keyword = "halal"
            }else if(mySettings.vegetarian){
                keyword = "vegetarian"
            }else{
                keyword = ""
            }
        }

        fun nearbyPlacesRequest(nearbyRestaurants: MutableList<Restaurant>, callback: ()->Unit): JsonObjectRequest {
            val url =
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$latitude,$longitude&radius=$radius&keyword=$keyword&type=restaurant&key=$API_KEY"
            val request = JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener<JSONObject?>() {
                        response -> if(response != null){

                    val results = response.getJSONArray("results")
                    for(i in 0..(results.length()-1)){
                        val restaurant = results.getJSONObject(i)
//                        Log.d("API Helper",restaurant.toString())
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
                        var photos: JSONArray
                        var photoUrl: String = "https://www.nicepng.com/png/detail/214-2148603_you-eat-ready-to-eat-food-icon.png"
                        if(restaurant.has("photos")) {
                            photos = restaurant.getJSONArray("photos")
                            if (photos.length() != 0) {
                                val reference = photos.getJSONObject(0).getString("photo_reference")
                                photoUrl = getPhotoUrl(reference)
                            }
                        }

                        nearbyRestaurants.add(
                            Restaurant(name = name, place_id = place_id, address = address,
                            price_level = price_level, rating = rating, user_ratings_total = user_ratings_total,
                            opening_now = opening_now, url = photoUrl, latitude = latitude, longitude = longitude)
                        )
//                        Log.d("Json Init", "$name\n$place_id\n$address\n$price_level\n$rating\n$opening_now\n$url\n$latitude, $longitude")
                    }
                    callback()

                }
                }, Response.ErrorListener { error -> error.printStackTrace() })
            return request
        }

        fun placeDetailsRequest(place_id: String, callback: (photoRefs: MutableList<String>) -> Unit) : JsonObjectRequest {
            val url = "https://maps.googleapis.com/maps/api/place/details/json?place_id=$place_id&fields=photos&key=$API_KEY"
            val placePhotos = mutableListOf<String>()
            val request = JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener<JSONObject?>() {
                        response -> if (response != null) {
                    try{
                        val result = response.getJSONObject("result")
                        val photos = result.getJSONArray("photos")
                        for(i in 0 until photos.length()){
                            val photoReference = photos.getJSONObject(i).getString("photo_reference")
                            placePhotos.add(photoReference)
                        }
                        callback(placePhotos)
                    }catch(e : Exception){
                        e.printStackTrace()
                    }
                }
                }, Response.ErrorListener { error -> error.printStackTrace() })
            return request
        }

        fun getPhotoUrl(photo_reference : String) : String{
            val maxWidth = 400
            val maxHeight = 400
            var url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=$maxWidth&maxHeight=$maxHeight&photoreference=$photo_reference&key=$API_KEY"
            return url
        }

        fun getPhotoReferenceFromUrl(photoUrl : String) : String{
            var photo_reference = photoUrl.substringBefore("&key=")
            photo_reference = photo_reference.substringAfter("reference=")
            return photo_reference
        }
    }


}