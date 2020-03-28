package com.example.myapplication

class UserSettings(val uid: String, val halal: Boolean, val vegetarian: Boolean, val radius: Int){

    constructor() : this("", false, false, 1){

}
}


// [END usersettings_class]
