package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

private val PHOTO_STRS = listOf("https://media-cdn.tripadvisor.com/media/photo-s/0f/b2/5f/35/this-is-what-kfc-is-famous.jpg", "https://imgix.bustle.com/uploads/image/2019/4/9/e5e17083-273e-40f5-91cf-63a5ca339e99-ea3557c8-71a1-48e8-967f-4c166054baab-pizza-image_no-text.jpg?w=1020&h=574&fit=crop&crop=faces&auto=format&q=70")


class PhotoList : Fragment() {
    private lateinit var adapter: PhotoListAdapter
    private lateinit var photoPager: ViewPager2
    private lateinit var place_id : String

    private lateinit var ref: DatabaseReference
    private var vote: Int = 0

    companion object {
        const val TAG = "Voting"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        place_id = (activity as? ScrollingActivity)?.place_id.toString()
        Log.d("PhotoList.kt", "successfully obtained place id from parent activity")

        return inflater.inflate(R.layout.fragment_photo_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = PhotoListAdapter()
        adapter.setItemList(PHOTO_STRS)
        photoPager = view.findViewById(R.id.photo_pager)
        photoPager.adapter = adapter

        val upvote = view.findViewById<Button>(R.id.UpvoteButton)
        upvote.setOnClickListener {
            // photoreference to be changed to getCurPhotoReference
            // not using it now because firebase doesn't allow special characters to be stored
            upvotePhoto(place_id, "photoreference1")
        }
        val downvote = view.findViewById<Button>(R.id.DownvoteButton)
        downvote.setOnClickListener{
            // photoreference to be changed to getCurPhotoReference
            // not using it now because firebase doesn't allow special characters to be stored
            downvotePhoto(place_id, "photoreference2")
        }
    }

    private fun getCurPhotoReference() : String{
        val photoUrl = adapter.getItemUrlAt(photoPager.currentItem)
        val photoReference = APIHelper.getPhotoReferenceFromUrl(photoUrl)
        Log.d("PhotoList.kt", "from $photoUrl to $photoReference")
        return photoReference
    }

    //get vote
    fun getVote(placeId: String, photoId: String){
        ref = FirebaseDatabase.getInstance().getReference("Restaurant")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.child(placeId).child(photoId)!!.exists()) {
                    vote = dataSnapshot.child(placeId).child(photoId).getValue<Int>()!!
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "voting:Cancelled", databaseError.toException())
                // ...
            }
        })
    }
    // vote photo
    val database = Firebase.database
    val databaseRes = database.getReference("Restaurant")

    //function to add vote to photo
    fun upvotePhoto(placeId: String, photoId: String){
        getVote(placeId, photoId)
        databaseRes.child(placeId).child(photoId).setValue(vote+1)
    }

    fun downvotePhoto(placeId: String, photoId: String){
        getVote(placeId, photoId)
        databaseRes.child(placeId).child(photoId).setValue(vote-1)
    }

}

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

//class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//    var photo: ImageView = view.findViewById(R.id.food_photo)
//}



class PhotoViewHolder constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    constructor(parent: ViewGroup) :
            this(LayoutInflater.from(parent.context).inflate(R.layout.photo_item, parent, false))

    var photo: ImageView = itemView.findViewById(R.id.food_photo)

//    fun bind(photo: String) {
//        itemView.food_photo.setImageResource(photo)
//    }
}
