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
    private val old: List<Restaurant>,
    private val new: List<Restaurant>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return old.size
    }

    override fun getNewListSize(): Int {
        return new.size
    }

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition].id == new[newPosition].id
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition] == new[newPosition]
    }

}
// not sure what above code is for


data class Restaurant(
    val id: Long = counter++,
    val name: String,
    val distance: Double,
    val url: String
) {
    companion object {
        private var counter = 0L
    }
}

class CardStackAdapter(
    private var restaurants: List<Restaurant> = emptyList()
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_restaurant_card, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurant = restaurants[position]
        holder.name.text = "${restaurant.id}. ${restaurant.name}"
        holder.distance.text = restaurant.distance.toString()
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

    fun setRestaurants(restaurants: List<Restaurant>) {
        this.restaurants = restaurants
    }

    fun getRestaurants(): List<Restaurant> {
        return restaurants
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.item_name)
        var distance: TextView = view.findViewById(R.id.item_distance)
        var photo: ImageView = view.findViewById(R.id.item_image)
    }

}