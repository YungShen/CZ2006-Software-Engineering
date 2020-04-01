package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DefaultItemAnimator
import com.android.volley.RequestQueue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yuyakaido.android.cardstackview.*
import java.io.Serializable


data class Restaurant(var place_id:String,
                      val name: String,
                      val url: String,
                      val latitude: Double,
                      val longitude: Double,
                      val address: String,
                      val price_level: Int,
                      val rating: Double,
                      val user_ratings_total: Int,
                      val opening_now: Boolean) :Serializable

class Main_Page : AppCompatActivity(), CardStackListener {

    private val SETTINGS_ACTIVITY_REQUEST_CODE = 0
    private val MAP_ACTIVITY_REQUEST_CODE = 1
    private val SHORTLISTED_ACTIVITY_REQUEST_CODE = 2

    private val cardStackView by lazy { findViewById<CardStackView>(R.id.card_stack_view) }
    private val manager by lazy { CardStackLayoutManager(this, this) }
    private val adapter by lazy { CardStackAdapter() }
    private lateinit var mQueue : RequestQueue

    private var viewedRestaurants = mutableListOf<String>()
    private var shortlistedRestaurants = ArrayList<Restaurant>()
    private var restaurantsFromAPI = mutableListOf<Restaurant>()
    private var currentRestaurant = 0


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swiping)

        findViewById<CardView>(R.id.IncreaseRadiusCard).visibility = INVISIBLE
        setupButtons()
        setupCardStackView()

        mQueue = SingletonObjects.getInstance(this).requestQueue
        mQueue.add(APIHelper.nearbyPlacesRequest(
            restaurantsFromAPI,
            { setRestaurantCallback(it) },
            ""))
        val userAddress = intent.getStringExtra("user_address")
        val textView: TextView = findViewById(R.id.LocationText)
        textView.text = userAddress
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setRestaurantCallback(nextPageToken: String){
        Log.d("Main_Page.kt", "Current restaurantsFromAPI size: ${restaurantsFromAPI.size}")
        if(nextPageToken != ""){
            Log.d("Main_Page.kt", "setRestaurantsCallback: added one more nearbySearchRequest to queue")
            mQueue.add(APIHelper.nearbyPlacesRequest(
                restaurantsFromAPI,
                { setRestaurantCallback(it) },
                nextPageToken
            ))
        }else{
            // set all restaurants one shot
            adapter.setRestaurants(restaurantsFromAPI)
            if(restaurantsFromAPI.size == 0){
                val increaseRadiusCard = findViewById<CardView>(R.id.IncreaseRadiusCard)
                increaseRadiusCard.visibility = VISIBLE
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun reSearch(){
        Log.d("Main_Page.kt", "Perform Re-search")
        restaurantsFromAPI.clear()
        currentRestaurant = 0
        mQueue.add(APIHelper.nearbyPlacesRequest(restaurantsFromAPI, { setRestaurantCallback(it) }, ""))
        val increaseRadiusCard = findViewById<CardView>(R.id.IncreaseRadiusCard)
        increaseRadiusCard.visibility = INVISIBLE
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == SETTINGS_ACTIVITY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                val settingsChanged = data!!.getBooleanExtra("settingsChanged", false)
                if(settingsChanged){
                    reSearch()
                }
            }
        }else if (requestCode == MAP_ACTIVITY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                val address = data!!.getStringExtra("user_address")
                val textView: TextView = findViewById(R.id.LocationText)

                if(address != null && address != ""){
                    Log.d("Main_Page.kt", "address is $address")
                    textView.text = address
                    reSearch()
                }
            }
        }else if(requestCode == SHORTLISTED_ACTIVITY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                val arr  = intent.getSerializableExtra("restaurant_list_to_pass_back")
                if(arr != null){
                    shortlistedRestaurants = arr as ArrayList<Restaurant>
                }else{
                    shortlistedRestaurants = ArrayList<Restaurant>()
                }
                Log.d("Main_Page.kt", "passed back shortlisted restaurant of length ${shortlistedRestaurants.size}")
            }
        }
    }

    override fun onCardSwiped(direction: Direction) {
        if(direction == Direction.Right || direction == Direction.Top){
            var shortlistedPreviously = false
            for(i in 0 until shortlistedRestaurants.size){
                if(restaurantsFromAPI[currentRestaurant].place_id == shortlistedRestaurants[i].place_id){
                    shortlistedPreviously = true
                }
            }
            if(!shortlistedPreviously){
                shortlistedRestaurants.add(restaurantsFromAPI[currentRestaurant])
                Log.d("Main_Page", "Shortlisted ${shortlistedRestaurants.last().name}")
            }
        }
        if(direction == Direction.Top){
            val intent = Intent(this, FinalActivity::class.java).putExtra("restaurant_to_final",restaurantsFromAPI[currentRestaurant])
            startActivity(intent)
        }
        viewedRestaurants.add(restaurantsFromAPI[currentRestaurant].place_id)
        currentRestaurant++

        if(currentRestaurant == restaurantsFromAPI.size){
            Log.d("Main_Page.kt", "No more restaurants")
            findViewById<CardView>(R.id.IncreaseRadiusCard).visibility = VISIBLE
        }
    }

    override fun onCardDragging(direction: Direction, ratio: Float) {
//        Log.d("CardStackView", "onCardDragging: d = ${direction.name}, r = $ratio")
    }

    override fun onCardRewound() {
//        Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")
    }

    override fun onCardCanceled() {
//        Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
    }

    override fun onCardAppeared(view: View, position: Int) {
//        val textView = view.findViewById<TextView>(R.id.item_name)
//        Log.d("CardStackView", "onCardAppeared: ($position) ${textView.text}")
    }

    override fun onCardDisappeared(view: View, position: Int) {
//        val textView = view.findViewById<TextView>(R.id.item_name)
//        Log.d("CardStackView", "onCardDisappeared: ($position) ${textView.text}")
    }

    private fun setupCardStackView() {
        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
        cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupButtons(){

        fun goToShortlisted(){
            val intent = Intent(this, ShortlistedRestaurantsActivity::class.java)
            intent.putExtra("restaurant_list_to_pass", shortlistedRestaurants )
            startActivityForResult(intent, SHORTLISTED_ACTIVITY_REQUEST_CODE)
        }

        var button = findViewById<Button>(R.id.SettingsButton)
        button.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivityForResult(intent, SETTINGS_ACTIVITY_REQUEST_CODE)
        }
        val clickableCard = findViewById<CardView>(R.id.SetLocationCard)
        clickableCard.setOnClickListener {
            val intent = Intent(this, MapsActivityCurrentPlace::class.java)
            startActivityForResult(intent, MAP_ACTIVITY_REQUEST_CODE)
        }
        button = findViewById(R.id.ViewShortlistedButton)
        button.setOnClickListener {
            goToShortlisted()
        }
        button = findViewById(R.id.NotIncreaseRadiusButton)
        button.setOnClickListener {
            goToShortlisted()
        }
        button = findViewById(R.id.IncreaseRadiusButton)
        button.setOnClickListener {
            if(mySettings.radius < 5){
                mySettings.radius += 1
                Toast.makeText(this@Main_Page,"Successfully increased your search radius by 1km!", Toast.LENGTH_LONG).show()
                reSearch()
            }else{
                Toast.makeText(this@Main_Page,"Your already have the largest search radius!", Toast.LENGTH_LONG).show()
            }
        }
        var imageButton = findViewById<ImageButton>(R.id.DiscardButton)
        imageButton.setOnClickListener {
            if(cardStackView.adapter?.itemCount != 0){
                val setting = SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Left)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(AccelerateInterpolator())
                    .build()
                manager.setSwipeAnimationSetting(setting)
                cardStackView.swipe()
            }
        }
        imageButton = findViewById(R.id.ShortlistButton)
        imageButton.setOnClickListener {
            if(cardStackView.adapter?.itemCount != 0){
                val setting = SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Right)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(AccelerateInterpolator())
                    .build()
                manager.setSwipeAnimationSetting(setting)
                cardStackView.swipe()
            }
        }
        button = findViewById(R.id.SuperlikeButton)
        button.setOnClickListener {
            if(cardStackView.adapter?.itemCount != 0){
                val setting = SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Top)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(AccelerateInterpolator())
                    .build()
                manager.setSwipeAnimationSetting(setting)
                cardStackView.swipe()
            }
        }

    }

    val database = Firebase.database
    val databaseUR = database.getReference("UsersRestaurant")

    //function to add restaurant to shortlist
    fun shortlistRestaurant(resId: String){
        val key: String? = databaseUR.push().key
        databaseUR.child(mySettings.uid).child("Shortlist").child(key!!).setValue(resId)
        databaseUR.child(mySettings.uid).child("Viewed").child(key!!).setValue(resId)
    }

    fun rejectRestaurant(resId: String){
        val key: String? = databaseUR.push().key
        databaseUR.child(mySettings.uid).child("Viewed").child(key!!).setValue(resId)
    }

}
