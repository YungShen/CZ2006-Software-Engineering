package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2

class PhotoList : Fragment() {
    private lateinit var adapter: PhotoListAdapter
    private lateinit var photoPager: ViewPager2
    private lateinit var place_id : String

    companion object {
        const val TAG = "Voting"
        const val MAX_PHOTO = 5
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        place_id = (activity as? ScrollingActivity)?.place_id.toString()
        return inflater.inflate(R.layout.fragment_photo_list, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = PhotoListAdapter()
        photoPager = view.findViewById(R.id.photo_pager)
        photoPager.adapter = adapter

        val requestQueue = SingletonObjects.getInstance(this.requireContext()).requestQueue
        requestQueue.add(APIHelper.placeDetailsPhotosRequest(place_id
        ) { photoRefs ->
            // the returned list is a bunch of photoReferences, not urls
            if (photoRefs.size < MAX_PHOTO) {
                setPhotoRefsToAdapter(photoRefs)
            } else {
                photoRefs.shuffle()
                // dynamically insert to the adapter if the vote is not negative
                photoRefs.forEach { item ->
                    DatabaseHelper.getVote(place_id, item) { numVote, photoRef -> addToAdapterCallback(numVote, photoRef) }
                }
            }
        })

        val upvote = view.findViewById<Button>(R.id.UpvoteButton)
        upvote.setOnClickListener {
            val photoRef = getCurPhotoReference()
            DatabaseHelper.upvotePhoto(place_id, photoRef)
            Toast.makeText(activity, "Your voting response will be recorded.",
                Toast.LENGTH_SHORT).show()
        }
        val downvote = view.findViewById<Button>(R.id.DownvoteButton)
        downvote.setOnClickListener{
            val photoRef = getCurPhotoReference()
            DatabaseHelper.downvotePhoto(place_id, photoRef)
            Toast.makeText(activity, "Your voting response will be recorded.",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun addToAdapterCallback(numVote: Int, photoRef: String){
        if(numVote >= 0 && adapter.itemCount < MAX_PHOTO){
            adapter.addItemToList(APIHelper.getPhotoUrl(photoRef))
        }
    }

    private fun setPhotoRefsToAdapter(photoRefs : MutableList<String>){
        for(i in 0 until photoRefs.size){
            photoRefs[i] = APIHelper.getPhotoUrl(photoRefs[i])
        }
        adapter.setItemList(photoRefs.toList())
    }

    private fun getCurPhotoReference() : String{
        val photoUrl = adapter.getItemAt(photoPager.currentItem)
        if(photoUrl == ""){
            return ""
        }
        return APIHelper.getPhotoReferenceFromUrl(photoUrl)
    }

}
