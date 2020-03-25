package com.example.myapplication

import com.google.firebase.database.IgnoreExtraProperties

// [START photo_class]
@IgnoreExtraProperties
data class Photo(
    var photoId: String? = "",
    var vote: Int? = 0
)
// [END photo_class]