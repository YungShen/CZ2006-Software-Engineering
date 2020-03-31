package com.example.myapplication

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


// not sure what is this for
import androidx.recyclerview.widget.DiffUtil
import java.io.Serializable


class CardStackAdapter(
    private var restaurants: MutableList<Restaurant> = mutableListOf()
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

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
            v -> v.context.startActivity(Intent(v.context, ScrollingActivity::class.java).putExtra("restaurant_to_pass",restaurant)
        )

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