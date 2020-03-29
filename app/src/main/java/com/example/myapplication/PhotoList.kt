package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


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
        return inflater.inflate(R.layout.fragment_photo_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = PhotoListAdapter()
        photoPager = view.findViewById(R.id.photo_pager)
        photoPager.adapter = adapter

        val requestQueue = SingletonObjects.getInstance(this.requireContext()).requestQueue
        requestQueue.add(APIHelper.placeDetailsRequest(place_id
        ) {
            // if it.size > 5
            // get current database votes
            // calculate how many to randomize
            // exclude the photos that has either positive or negative votes in the database
            // randomize the remaining photos
            // pass the list to the adapter
            adapter.setItemList(it.toList())
        })

        val upvote = view.findViewById<Button>(R.id.UpvoteButton)
        upvote.setOnClickListener {
            upvotePhoto(place_id, getCurPhotoReference())
        }
        val downvote = view.findViewById<Button>(R.id.DownvoteButton)
        downvote.setOnClickListener{
            downvotePhoto(place_id, getCurPhotoReference())
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

