package com.example.myapplication

import com.google.firebase.database.IgnoreExtraProperties

// [START usersettings_class]
@IgnoreExtraProperties
data class UserSettings(
    var email: String? = "",
    var halal: Boolean? = false,
    var vegetarian: Boolean? = false,
    var radius: Int? = 2

    //var history: Array<String>? = arrayOf<String>()
)

// [END usersettings_class]