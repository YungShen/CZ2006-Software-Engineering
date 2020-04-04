package com.example.myapplication

import java.io.Serializable

data class Restaurant(var place_id:String,
                      val name: String,
                      val url: String,
                      val latitude: Double,
                      val longitude: Double,
                      val address: String,
                      val price_level: Int,
                      val rating: Double,
                      val user_ratings_total: Int,
                      val opening_now: Boolean) : Serializable

