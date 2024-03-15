package com.example.chatwave.data

import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext

open class Event<out T>(val content: T) {
    var hasBeenHandled = false
    fun getContentOrNull():T?{
        return if (hasBeenHandled) null
        else{
            hasBeenHandled= true
            content
        }
    }
}