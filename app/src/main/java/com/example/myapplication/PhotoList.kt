package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
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
    private val database = Firebase.database
    private val databaseRes = database.getReference("Restaurant")
    private var vote: Int = 0

    companion object {
        const val TAG = "Voting"
        const val MAX_PHOTO = 5
        const val MAX_VOTED_PHOTO = 3
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
                setPhotoUrlsToAdapter(photoRefs)
            } else {
                photoRefs.shuffle()
                // dynamically insert to the adapter if the vote is not negative
                photoRefs.forEach { item ->
                    getVote(place_id, item) { numVote, photoRef -> addToAdapterCallback(numVote, photoRef) }
                }

            }

//                val photosWithVote = ConcurrentLinkedQueue<PhotoWithVote>()
//                runBlocking {
//
//                    photoRefs.forEach {item ->
//                        launch(Dispatchers.Default){
//                            getVote(place_id, item)
//                            // hard coded super long delay still sometimes don't work zzz
//                            delay(5000)
//                            photosWithVote.add(PhotoWithVote(item, vote))
//                            Log.d("PhotoList.kt", "photosWithVote inserted photo of vote ${photosWithVote.last().vote}")
//                        }
//                    }
//                }
//                photosWithVote.forEach {
//                        item -> println("photosWithVote's vote: ${item.vote}")
//                }
//                setSortedWithRandomizedPhotos(photoRefs, photosWithVote)
        })

        val upvote = view.findViewById<Button>(R.id.UpvoteButton)
        upvote.setOnClickListener {
            val photoRef = getCurPhotoReference()
            upvotePhoto(place_id, photoRef)
        }
        val downvote = view.findViewById<Button>(R.id.DownvoteButton)
        downvote.setOnClickListener{
            val photoRef = getCurPhotoReference()
            downvotePhoto(place_id, photoRef)
        }
    }

    private fun addToAdapterCallback(numVote: Int, photoRef: String){
        if(numVote >= 0 && adapter.itemCount < MAX_PHOTO){
            adapter.addItemToList(APIHelper.getPhotoUrl(photoRef))
        }
    }

    //get vote
    private fun getVote(placeId: String, photoRef: String, callback: (Int, String)->Unit){
        ref = FirebaseDatabase.getInstance().getReference("Restaurant")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(placeId).child(photoRef)!!.exists()) {
                    vote = 0
                    for (u in dataSnapshot.child(placeId).child(photoRef).children) {
                        Log.d("getVote", "Vote before: $vote")
                        vote += u.getValue<Int>()!!
                        Log.d("getVote", "Vote after: $vote")
                    }
                    Log.d("getVote", "The vote is: $vote")
                    callback(vote, photoRef)
                }else{
                    callback(0, photoRef)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "voting:Cancelled", databaseError.toException())
                // ...
            }
        })
    }

    //function to add vote to photo
    private fun upvotePhoto(placeId: String, photoId: String){
        databaseRes.child(placeId).child(photoId).child(mySettings.uid).setValue(1)
    }

    private fun downvotePhoto(placeId: String, photoId: String){
        val key = databaseRes.child(placeId).child(photoId).push().key
        databaseRes.child(placeId).child(photoId).child(mySettings.uid).setValue(-1)
    }

//    @RequiresApi(Build.VERSION_CODES.N)
//    fun setSortedWithRandomizedPhotos(it: MutableList<String>, photosWithVote: ConcurrentLinkedQueue<PhotoWithVote>) {
//
//        Log.d("PhotoList.kt", "In setSortedWithRandomizedPhotos")
//        Log.d("PhotoList.kt", "photosWithVote.size = ${photosWithVote.size}")
//
//        val sortedList = photosWithVote.sortedBy { item-> item.vote }
//
//        val displayPhotoList = mutableListOf<String>()
//        for(i in 0 until MAX_VOTED_PHOTO){
//            if(sortedList[i].vote > 0){
//                displayPhotoList.add(sortedList[i].photoRef)
//                Log.d("PhotoList.kt", "Added photo of vote ${sortedList[i].vote}")
//            }else{
//                Log.d("PhotoList.kt", "This photo is of ${sortedList[i].vote}, break")
//                break
//            }
//        }
//        val notUsedPhotos = sortedList.subList(fromIndex = displayPhotoList.size, toIndex = photosWithVote.size)
//        val nonNegList = notUsedPhotos.filter { item-> item.vote >= 0 }
//        val numToRandom = MAX_PHOTO-displayPhotoList.size
//        if(nonNegList.size > numToRandom){
//            val randomList = (0 until nonNegList.size).shuffled().take(numToRandom)
//            for(i in 0 until numToRandom){
//                displayPhotoList.add(nonNegList[randomList[i]].photoRef)
//            }
//        }else{
//            for(i in 0 until nonNegList.size){
//                displayPhotoList.add(nonNegList[i].photoRef)
//            }
//        }
//        setPhotoUrlsToAdapter(displayPhotoList)
//    }

    private fun setPhotoUrlsToAdapter(photoRefs : MutableList<String>){
        for(i in 0 until photoRefs.size){
            photoRefs[i] = APIHelper.getPhotoUrl(photoRefs[i])
        }
        adapter.setItemList(photoRefs.toList())
    }

    private fun getCurPhotoReference() : String{
        val photoUrl = adapter.getItemUrlAt(photoPager.currentItem)
        if(photoUrl == ""){
            return ""
        }
        return APIHelper.getPhotoReferenceFromUrl(photoUrl)
    }


}

//data class PhotoWithVote(
//    val photoRef : String,
//    val vote: Int
//)
