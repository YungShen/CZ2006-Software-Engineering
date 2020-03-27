package com.example.myapplication

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ShortlistedRestaurantAdapter (private val shortlistedItems: MutableList<String>) : RecyclerView.Adapter< ShortlistedRestaurantAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var restaurantName: TextView = itemView.findViewById<TextView>(R.id.ShortlistedRestaurantName)
        var deleteButton: ImageButton = itemView.findViewById<ImageButton>(R.id.TrashbinButton)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        val inflator = LayoutInflater.from(parent.context)
        val shortlistedItemView = inflator.inflate(R.layout.shortlisted_item, parent,false)
        return ViewHolder(shortlistedItemView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurantName = shortlistedItems[position]
        holder.restaurantName.text = restaurantName
        holder.restaurantName.setOnClickListener(View.OnClickListener {
                v -> v.context.startActivity(Intent(v.context, FinalActivity::class.java))
        })
        holder.deleteButton.setOnClickListener(View.OnClickListener {
            shortlistedItems.removeAt(position)
            notifyDataSetChanged()
        })
    }

    override fun getItemCount() :Int {
        return  shortlistedItems.size
    }

    public fun addRestaurant(name: String){
        shortlistedItems.add(name)
    }

}