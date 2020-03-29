package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PhotoListAdapter : RecyclerView.Adapter<PhotoViewHolder>() {

    private var photos : List<String> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(parent)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        Glide.with(holder.photo)
            .load(photos[position])
            .into(holder.photo)
    }

    fun setItemList(photos: List<String>) {
        this.photos = photos
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = photos.size

    fun getItemUrlAt(position: Int) : String{
        return photos[position]
    }

}

class PhotoViewHolder constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    constructor(parent: ViewGroup) :
            this(LayoutInflater.from(parent.context).inflate(R.layout.photo_item, parent, false))

    var photo: ImageView = itemView.findViewById(R.id.food_photo)

}
