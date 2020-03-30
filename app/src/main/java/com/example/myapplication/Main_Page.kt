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

    private val cardStackView by lazy { findViewById<CardStackView>(R.id.card_stack_view) }
    private val manager by lazy { CardStackLayoutManager(this, this) }
    private val adapter by lazy { CardStackAdapter() }
    private lateinit var mQueue : RequestQueue

    private var viewedRestaurants = mutableListOf<String>()
    private var shortlistedRestaurants = ArrayList<Restaurant>()
    private var restaurantsFromAPI = mutableListOf<Restaurant>()
    private var currentRestaurant = 0

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setRestaurantCallback(){
        restaurantsFromAPI.removeIf {
            viewedRestaurants.contains(it.place_id)
        }
        Log.d("Main_Page.kt", "current swipable restaurant list length: ${restaurantsFromAPI.size}")
        if(restaurantsFromAPI.size==0){
            Toast.makeText(this@Main_Page,"No more distinct restaurants...", Toast.LENGTH_LONG).show()
        }else{
            adapter.setRestaurants(restaurantsFromAPI)
            cardStackView.adapter?.notifyDataSetChanged()
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swiping)

        findViewById<CardView>(R.id.IncreaseRadiusCard).visibility = INVISIBLE
        setupButtons()
        setupCardStackView()

        mQueue = SingletonObjects.getInstance(this).requestQueue
        mQueue.add(APIHelper.nearbyPlacesRequest(restaurantsFromAPI) { setRestaurantCallback() })
        val userAddress = intent.getStringExtra("user_address")
        val textView: TextView = findViewById<TextView>(R.id.LocationText)
        textView.text = userAddress
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == SETTINGS_ACTIVITY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                val settingsChanged = data!!.getBooleanExtra("settingsChanged", false)
                Log.d("Main_Page.kt", "settingsChanged passed back from SettingsActivity: $settingsChanged")
                if(settingsChanged){
                    restaurantsFromAPI.clear()
                    currentRestaurant = 0
                    mQueue.add(APIHelper.nearbyPlacesRequest(restaurantsFromAPI) { setRestaurantCallback() })
                }
            }
        }
    }

    override fun onCardSwiped(direction: Direction) {
        if(direction == Direction.Right || direction == Direction.Top){
            shortlistedRestaurants.add(restaurantsFromAPI[currentRestaurant])
            Log.d("Main_Page", "Shortlisted ${shortlistedRestaurants.last().name}")
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
            startActivity(intent)
        }

        var button = findViewById<Button>(R.id.SettingsButton)
        button.setOnClickListener(
            View.OnClickListener {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivityForResult(intent, SETTINGS_ACTIVITY_REQUEST_CODE)
            }
        )
        val clickableCard = findViewById<CardView>(R.id.SetLocationCard)
        clickableCard.setOnClickListener(
            View.OnClickListener {
                val intent = Intent(this, MapsActivityCurrentPlace::class.java)
                startActivity(intent)
            }
        )
        button = findViewById<Button>(R.id.ViewShortlistedButton)
        button.setOnClickListener(
            View.OnClickListener {
                goToShortlisted()
            }
        )
        button = findViewById<Button>(R.id.NotIncreaseRadiusButton)
        button.setOnClickListener(
            View.OnClickListener {
                goToShortlisted()
            }
        )
        button = findViewById<Button>(R.id.IncreaseRadiusButton)
        button.setOnClickListener(
            View.OnClickListener {
                if(mySettings.radius < 5){
                    mySettings.radius += 1
                    Toast.makeText(this@Main_Page,"Successfully increased your search radius by 1km!", Toast.LENGTH_LONG).show()
                    restaurantsFromAPI.clear()
                    currentRestaurant = 0
                    mQueue.add(APIHelper.nearbyPlacesRequest(restaurantsFromAPI) { setRestaurantCallback() })
                }else{
                    Toast.makeText(this@Main_Page,"Your already have the largest search radius!", Toast.LENGTH_LONG).show()
                }
            }
        )
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
        imageButton = findViewById<ImageButton>(R.id.ShortlistButton)
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
        button = findViewById<Button>(R.id.SuperlikeButton)
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
