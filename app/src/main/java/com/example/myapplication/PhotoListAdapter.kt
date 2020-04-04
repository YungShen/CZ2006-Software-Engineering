package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PhotoListAdapter : RecyclerView.Adapter<PhotoViewHolder>() {

    private var photos = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(parent)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        Glide.with(holder.photo)
            .load(photos[position])
            .into(holder.photo)
    }

    fun setItemList(photos: List<String>) {
        this.photos = photos as MutableList<String>
        notifyDataSetChanged()
    }

    fun addItemToList(photo: String){
        photos.add(photo)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = photos.size

    fun getItemAt(position: Int) : String{
        if(position >= photos.size){
            return ""
        }
        return photos[position]
    }

}

class PhotoViewHolder constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    constructor(parent: ViewGroup) :
            this(LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false))

    var photo: ImageView = itemView.findViewById(R.id.food_photo)

}
