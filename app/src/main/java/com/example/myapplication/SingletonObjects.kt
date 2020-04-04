package com.example.myapplication

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class SingletonObjects constructor(context: Context){
    companion object {
        @Volatile
        private var INSTANCE: SingletonObjects? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: SingletonObjects(context).also {
                    INSTANCE = it
                }
            }
    }
    val requestQueue : RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }
}