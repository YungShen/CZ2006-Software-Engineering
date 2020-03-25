package com.example.myapplication

import com.google.firebase.database.IgnoreExtraProperties

// [START place_class]
@IgnoreExtraProperties
data class Place(
    var placeId: String? = "placeIdtester",
    var photo: Array<Photo>
)
// [END place_class]