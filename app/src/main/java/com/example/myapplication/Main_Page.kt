package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import com.android.volley.RequestQueue
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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
    private var currentRestaurants = mutableListOf<Restaurant>()







    private fun setRestaurantCallback(){
        adapter.setRestaurants(currentRestaurants)
        cardStackView.adapter?.notifyDataSetChanged()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swiping)
        setupButtons()
        setupCardStackView()

        mQueue = SingletonObjects.getInstance(this).requestQueue
        mQueue.add(APIHelper.nearbyPlacesRequest(currentRestaurants) { setRestaurantCallback() })
    }

    override fun onCardDragging(direction: Direction, ratio: Float) {
        Log.d("CardStackView", "onCardDragging: d = ${direction.name}, r = $ratio")
    }

    override fun onCardSwiped(direction: Direction) {
        if(direction == Direction.Right){
            shortlistedRestaurants.add(adapter.getRestaurant())
        }
        viewedRestaurants.add(adapter.getRestaurant().place_id)
        adapter.removeRestaurant()
        Log.d("CardStackView", "onCardSwiped: p = ${manager.topPosition}, d = $direction")
//        if (manager.topPosition == adapter.itemCount - 4) {
//            paginate()
//        }
    }

    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")
    }

    override fun onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
    }

    override fun onCardAppeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_name)
        Log.d("CardStackView", "onCardAppeared: ($position) ${textView.text}")
    }

    override fun onCardDisappeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_name)
        Log.d("CardStackView", "onCardDisappeared: ($position) ${textView.text}")
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

    private fun paginate() {
        val old = adapter.getRestaurants()
        val new = old.plus(currentRestaurants)
        val callback = SpotDiffCallback(old, new as MutableList<Restaurant>)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setRestaurants(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun reload() {
        val old = adapter.getRestaurants()
        val new = currentRestaurants
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setRestaurants(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun removeFirst(size: Int) {
        if (adapter.getRestaurants().isEmpty()) {
            return
        }

        val old = adapter.getRestaurants()
        val new = mutableListOf<Restaurant>().apply {
            addAll(old)
            for (i in 0 until size) {
                removeAt(manager.topPosition)
            }
        }
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setRestaurants(new)
        result.dispatchUpdatesTo(adapter)
    }


    private fun setupButtons(){
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
                val intent = Intent(this, ShortlistedRestaurantsActivity::class.java)
                intent.putExtra("restaurant_list_to_pass",shortlistedRestaurants )
                startActivity(intent)
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
