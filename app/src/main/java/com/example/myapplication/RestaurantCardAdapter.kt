package com.example.myapplication

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


// not sure what is this for
import androidx.recyclerview.widget.DiffUtil

class SpotDiffCallback(
    private val old: MutableList<Restaurant>,
    private val new: MutableList<Restaurant>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return old.size
    }

    override fun getNewListSize(): Int {
        return new.size
    }

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition].place_id == new[newPosition].place_id
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition] == new[newPosition]
    }

}
// not sure what above code is for


class CardStackAdapter(
    private var restaurants: MutableList<Restaurant> = mutableListOf()
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_restaurant_card, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurant = restaurants[position]
        holder.name.text = restaurant.name
        Glide.with(holder.photo)
            .load(restaurant.url)
            .into(holder.photo)
        holder.itemView.setOnClickListener {
            v -> v.context.startActivity(Intent(v.context, ScrollingActivity::class.java))
        }
    }

    override fun getItemCount(): Int {
        return restaurants.size
    }

    fun setRestaurants(restaurants: MutableList<Restaurant>) {
        this.restaurants = restaurants
    }

    fun getRestaurants(): MutableList<Restaurant> {
        return restaurants
    }

    fun getRestaurant(): Restaurant {
        return restaurants[0]
    }

    fun removeRestaurant(){
        restaurants.removeAt(0)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.item_name)
        var photo: ImageView = view.findViewById(R.id.item_image)
    }

}