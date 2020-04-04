package com.example.myapplication

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*

class DatabaseHelper{
    companion object{

        private lateinit var ref: DatabaseReference
        private val database = Firebase.database
        private val databaseRes = database.getReference("Restaurant")

        //get vote
        fun getVote(placeId: String, photoRef: String, callback: (Int, String)->Unit){
            val refRes = FirebaseDatabase.getInstance().getReference("Restaurant")

            refRes.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val photoSnapshot = dataSnapshot.child(placeId).child(photoRef)
                    if (photoSnapshot.exists()) {
                        var vote = 0
                        if(photoSnapshot.hasChildren()){
                            for (u in photoSnapshot.children) {
                                Log.d("getVote", "Vote before: $vote")
                                vote += u.getValue<Int>()!!
                                Log.d("getVote", "Vote after: $vote")
                            }
                        }else if(photoSnapshot.hasChild(mySettings.uid)){
                            vote = photoSnapshot.child(mySettings.uid).getValue<Int>()!!
                        }
                        Log.d("getVote", "The vote is: $vote")
                        callback(vote, photoRef)
                    }else{
                        callback(0, photoRef)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w(PhotoList.TAG, "voting:Cancelled", databaseError.toException())
                }
            })
        }

        //function to add vote to photo
        fun upvotePhoto(placeId: String, photoId: String){
            databaseRes.child(placeId).child(photoId).child(mySettings.uid).setValue(1)
        }

        fun downvotePhoto(placeId: String, photoId: String){
            databaseRes.child(placeId).child(photoId).child(mySettings.uid).setValue(-1)
        }

        private val auth = FirebaseAuth.getInstance()

        fun passwordReset(email: String, onSuccess: ()->Unit, onFailure: (Exception)->Unit){

            auth.sendPasswordResetEmail(email).addOnCompleteListener {
                if (it.isSuccessful){
                    onSuccess()
                }
            }.addOnFailureListener {
                onFailure(it)
            }

        }

        fun signIn(email: String, password: String, successCallback: ()->Unit, failureCallback: (String)->Unit){

            var error : String
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    if (!auth.currentUser?.isEmailVerified!!){
                        error = "Please verify your email address!"
                        failureCallback(error)
                        return@addOnCompleteListener
                    }

                    //retrieve user settings
                    val refUser = FirebaseDatabase.getInstance().getReference("Users")

                    //retrieve settings listener
                    refUser.addValueEventListener(object : ValueEventListener {

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
//                            Log.w(LoginActivity.TAG, "stage 1")
                            if(dataSnapshot!!.exists()) {
//                                Log.w(LoginActivity.TAG, "stage 2")
                                for (u in dataSnapshot.children){
//                                    Log.w(LoginActivity.TAG, "stage $u")
                                    if (u.key == it.result?.user?.uid) {
//                                        Log.w(LoginActivity.TAG, "stage 4")
                                        val currentSettings = u.getValue(UserSettings::class.java)!!
                                        mySettings.radius = currentSettings.radius
                                        mySettings.halal = currentSettings.halal
                                        mySettings.uid = currentSettings.uid
                                        mySettings.vegetarian = currentSettings.vegetarian

                                        Log.w(LoginActivity.TAG, "user settings retrieved")

                                        Log.w(LoginActivity.TAG, "halal ${currentSettings.halal}")
                                        Log.w(LoginActivity.TAG, "veg ${currentSettings.vegetarian}")
                                        Log.w(LoginActivity.TAG, "radius ${currentSettings.radius}")
                                    }
                                }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Getting Post failed, log a message
                            Log.w(LoginActivity.TAG, "settings:onCancelled", databaseError.toException())
                            // ...
                        }
                    })

                    successCallback()

                }
                .addOnFailureListener {
//                    Toast.makeText(this, "Failed to log in: ", Toast.LENGTH_SHORT).show()
                    failureCallback("Failed to log in: ${it.message}")
                }
        }

//            //create new User settings
//            private val database = Firebase.database

        private fun writeNewUser(userId: String) {
            val databaseNewUser = database.getReference("Users")
            val userSettings = UserSettings(userId, false, false, 1)
            databaseNewUser.child(userId).setValue(userSettings)
        }

        fun register(email: String, password: String, successCallback: () -> Unit, failureCallback: (String) -> Unit){
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    // else if successful
                    it.result?.user?.uid?.let { it1 -> writeNewUser(it1) }
                    Log.d(RegisterActivity.TAG, "Successfully created user with uid: ${it.result?.user?.uid}")

                    //send verification email
                    auth.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener {
                            // verification email sent successfully
                            if (it.isSuccessful){
                                successCallback()
                            }
                        }?.addOnFailureListener {
                            // verification email not sent
                            failureCallback("Failed to send verification email: ${it.message}")
                        }

                }
                .addOnFailureListener{
                    failureCallback("Failed to create user: ${it.message}")
                }

        }

        fun saveSettings(halal: Boolean, veg: Boolean, distance: Int){

            val databaseUsers = database.getReference("Users")

            databaseUsers.child(mySettings.uid).setValue(UserSettings(mySettings.uid, halal, veg, distance/1000))
            mySettings.halal = halal
            mySettings.vegetarian = veg
            mySettings.radius = distance/1000

            Log.d("DatabaseHelper", "after saveSettings")

        }



    }
}