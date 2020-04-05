package com.example.myapplication

import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.Response
import org.json.JSONArray

/*
* This class provides support in using Google Places API
* It creates JsonObjectRequest of the desired search request for the calling activity to put into volley RequestQueue
* It also has function that transforms photo_reference into url (that could be used to get the photo from Places Photos API)
*/
class APIHelper {

    companion object{

        private const val API_KEY = "AIzaSyATXIXpRO7l62cUt_vhiSzdOeSiiwKEnSU"
        private var radius = mySettings.radius*1000
        private var keyword = ""
        private var latitude = mySettings.locationOfUser.latitude
        private var longitude = mySettings.locationOfUser.longitude

        private fun adjustToUserSettings(){
            latitude = mySettings.locationOfUser.latitude
            longitude = mySettings.locationOfUser.longitude
            radius = mySettings.radius*1000
            keyword = if(mySettings.halal && mySettings.vegetarian){
                "halal,vegetarian"
            }else if(mySettings.halal){
                "halal"
            }else if(mySettings.vegetarian){
                "vegetarian"
            }else{
                ""
            }
        }

        fun nearbyPlacesRequest(
            restaurantsFromAPI: MutableList<Restaurant>,
            callback: (String)->Unit,
            nextPageToken: String): JsonObjectRequest {

            val url: String = if(nextPageToken != ""){
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=$API_KEY&pagetoken=$nextPageToken"
            }else{
                adjustToUserSettings()
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$latitude,$longitude&radius=$radius&keyword=$keyword&type=restaurant&key=$API_KEY"
            }
            Log.d("APIHelper.kt", "Performs search at $url")

            return JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener { response -> if(response != null){

                    if(response.has("error_message")){
                        Log.e("APIHelper", response.getString("error_message"))
                    }else if(response.getString("status") != "OK"){
                        Log.e("APIHelper", response.getString("status"))
                    }

                    val results = response.getJSONArray("results")
                    for(i in 0 until results.length()){
                        val restaurant = results.getJSONObject(i)
                        val name = restaurant.getString("name")
                        val place_id = restaurant.getString("place_id")
                        val address = if (restaurant.has("vicinity")){
                            restaurant.getString("vicinity")
                        }else{
                            ""
                        }
                        val location = restaurant.getJSONObject("geometry").getJSONObject("location")
                        val latitude = location.getDouble("lat")
                        val longitude = location.getDouble("lng")

                        var price_level : Int = -1
                        var rating : Double = -1.0
                        var user_ratings_total = -1
                        var opening_now = true

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
                        var photoUrl = "https://i.imgur.com/HvaA8JM.png"
                        if(restaurant.has("photos")) {
                            photos = restaurant.getJSONArray("photos")
                            if (photos.length() != 0) {
                                val reference = photos.getJSONObject(0).getString("photo_reference")
                                photoUrl = getPhotoUrl(reference)
                            }
                        }

                        restaurantsFromAPI.add(
                            Restaurant(name = name, place_id = place_id, address = address,
                                price_level = price_level, rating = rating, user_ratings_total = user_ratings_total,
                                opening_now = opening_now, url = photoUrl, latitude = latitude, longitude = longitude)
                        )
                    }

                    var nextPage = ""
                    if(response.has("next_page_token")){
                        nextPage = response.getString("next_page_token")
                    }
                    callback(nextPage)

                }
                }, Response.ErrorListener { error -> error.printStackTrace() })
        }


        fun placeDetailsPhotosRequest(place_id: String, callback: (photoRefs: MutableList<String>) -> Unit) : JsonObjectRequest {
            val url = "https://maps.googleapis.com/maps/api/place/details/json?place_id=$place_id&fields=photos&key=$API_KEY"
            val placePhotos = mutableListOf<String>()
            return JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener {
                        response -> if (response != null) {

                    if(response.has("error_message")){
                        Log.e("APIHelper", response.getString("error_message"))
                    }

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
        }

        fun placeDetailsOthersRequest(place_id: String, callback: (phoneNum : String, website: String) -> Unit) : JsonObjectRequest {
            val url = "https://maps.googleapis.com/maps/api/place/details/json?place_id=$place_id&fields=international_phone_number,website&key=$API_KEY"
            return JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener {
                        response -> if (response != null) {

                    if(response.has("error_message")){
                        Log.e("APIHelper", response.getString("error_message"))
                    }

                    var international_phone_number= ""
                    var website = ""
                    try{
                        val result = response.getJSONObject("result")
                        if(result.has("international_phone_number")){
                            international_phone_number = result.getString("international_phone_number")
                        }
                        if(result.has("website")){
                            website = result.getString("website")
                        }
                    }catch(e : Exception){
                        e.printStackTrace()
                    }
                    callback(international_phone_number, website)
                }
                }, Response.ErrorListener { error -> error.printStackTrace() })
        }


        fun getPhotoUrl(photo_reference : String) : String{
            val maxWidth = 400
            val maxHeight = 400
            return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=$maxWidth&maxHeight=$maxHeight&photoreference=$photo_reference&key=$API_KEY"
        }

        fun getPhotoReferenceFromUrl(photoUrl : String) : String{
            var photo_reference = photoUrl.substringBefore("&key=")
            photo_reference = photo_reference.substringAfter("reference=")
            return photo_reference
        }
    }


}