package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.opengl.Visibility
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

    // mock list of restaurants
//    private val mockRestaurants = listOf(
//        Restaurant(name="KFC", url="https://media-cdn.tripadvisor.com/media/photo-s/0f/b2/5f/35/this-is-what-kfc-is-famous.jpg"),
//        Restaurant(name="Pizza Hut", url="https://imgix.bustle.com/uploads/image/2019/4/9/e5e17083-273e-40f5-91cf-63a5ca339e99-ea3557c8-71a1-48e8-967f-4c166054baab-pizza-image_no-text.jpg?w=1020&h=574&fit=crop&crop=faces&auto=format&q=70"),
//        Restaurant(name="Subway", url="https://www.subway.com/~/media/Base_English/Images/FooterButtons/footer_popup-menu_group.jpg?la=en-SG&hash=06CEB4EF8DA4CDD18EF4D18E9A71098804A5A704"),
//        Restaurant(name="Starbucks", url="https://assets.grab.com/wp-content/uploads/sites/4/2019/03/14182747/starbucks-delivery-singapore-grabfood-700x700.jpg"),
//        Restaurant(name="McDonald's", url="https://image.shutterstock.com/image-photo/russia-saintpetersburg-december-24-2018-260nw-1265992426.jpg")
//    )
    private val cardStackView by lazy { findViewById<CardStackView>(R.id.card_stack_view) }
    private val manager by lazy { CardStackLayoutManager(this, this) }
    private val adapter by lazy { CardStackAdapter() }
    private lateinit var mQueue : RequestQueue

    // both of these store place_id string
    private var viewedRestaurants = mutableListOf<String>()
    private var shortlistedRestaurants = ArrayList<Restaurant>()
    private var RestaurantsFromAPI = mutableListOf<Restaurant>()
    private var currentRestaurant = 0

    private val userSettings = UserSettings()

    private fun setRestaurantCallback(){
        adapter.setRestaurants(RestaurantsFromAPI)
        cardStackView.adapter?.notifyDataSetChanged()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swiping)

        findViewById<CardView>(R.id.IncreaseRadiusCard).visibility = INVISIBLE
        setupButtons()
        setupCardStackView()

        mQueue = SingletonObjects.getInstance(this).requestQueue
        mQueue.add(APIHelper.nearbyPlacesRequest(RestaurantsFromAPI) { setRestaurantCallback() })
        val userAddress = intent.getStringExtra("user_address")
        val textView: TextView = findViewById<TextView>(R.id.LocationText)
        textView.text = userAddress
    }

    override fun onCardDragging(direction: Direction, ratio: Float) {
//        Log.d("CardStackView", "onCardDragging: d = ${direction.name}, r = $ratio")
    }

    override fun onCardSwiped(direction: Direction) {
        if(direction == Direction.Right || direction == Direction.Top){
            shortlistedRestaurants.add(RestaurantsFromAPI[currentRestaurant])
            Log.d("Main_Page", "Shortlisted ${shortlistedRestaurants.last().name}")
        }
        if(direction == Direction.Top){
            val intent = Intent(this, FinalActivity::class.java).putExtra("restaurant_to_final",RestaurantsFromAPI[currentRestaurant])
            startActivity(intent)
        }
        viewedRestaurants.add(RestaurantsFromAPI[currentRestaurant].place_id)
        currentRestaurant++

        if(currentRestaurant == RestaurantsFromAPI.size){
            Log.d("Main_Page.kt", "No more restaurants")
            findViewById<CardView>(R.id.IncreaseRadiusCard).visibility = VISIBLE
        }
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

    @SuppressLint("ShowToast")
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
                startActivity(intent)
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
                if(userSettings.radius < 5){
                    userSettings.radius += 1
                    Toast.makeText(this@Main_Page,"Successfully increased your search radius by 1km!", Toast.LENGTH_LONG).show()
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
