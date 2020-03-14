package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.photo_item.view.*

// TODO: Rename parameter arguments, choose names that match
private val PHOTO_INTS = listOf(R.drawable.kfc1, R.drawable.kfc2, R.drawable.kfc3, R.drawable.kfc4, R.drawable.kfc5)

class VotePhoto : Fragment() {
    private lateinit var adapter: PhotoListAdapter
    private lateinit var photoPager: ViewPager2
    private lateinit var upvoteButton: Button
    private lateinit var downvoteButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vote_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        adapter = PhotoListAdapter()
        adapter.setItemList(PHOTO_INTS)
        photoPager = view.findViewById(R.id.photo_pager)
        photoPager.adapter = adapter

        upvoteButton = view.findViewById(R.id.Upvote)
        downvoteButton = view.findViewById(R.id.Downvote)
        print("viewPager and adapter initialized")
    }
}

class PhotoListAdapter : RecyclerView.Adapter<PhotoViewHolder>() {

    private var list : List<Int> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(parent)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun setItemList(list: List<Int>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size

}

class PhotoViewHolder constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    constructor(parent: ViewGroup) :
            this(LayoutInflater.from(parent.context).inflate(R.layout.photo_item, parent, false))

    fun bind(photoId: Int) {
        itemView.food_photo.setImageResource(photoId)
    }
}

//class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//}
