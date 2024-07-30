package com.codewithkael.twitchwithwebrtc.utils

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import java.util.UUID

@HiltAndroidApp
class MyApplication : Application() {
    companion object {
        val STREAM_ID =  UUID.randomUUID().toString().substring(0,5)
        var REMOTE_CALL_ID : Int? = 0
    }
}