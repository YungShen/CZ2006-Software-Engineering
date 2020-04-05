package com.example.myapplication

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class RestaurantCardStackAdapter(
    private var restaurants: MutableList<Restaurant> = mutableListOf()
) : RecyclerView.Adapter<RestaurantCardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_restaurant_card, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val restaurant = restaurants[position]

        //send restaurant from here

        holder.name.text = restaurant.name
        Glide.with(holder.photo)
            .load(restaurant.url)
            .into(holder.photo)
        holder.itemView.setOnClickListener {
            v ->
            val intent = Intent(v.context, ScrollingActivity::class.java).putExtra("restaurant_to_pass",restaurant)
            v.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return restaurants.size
    }

    fun setRestaurants(restaurants: MutableList<Restaurant>) {
        this.restaurants = restaurants
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.item_name)
        var photo: ImageView = view.findViewById(R.id.item_image)
    }

}