package com.codewithkael.twitchwithwebrtc.utils

import com.codewithkael.twitchwithwebrtc.utils.MyApplication.Companion.STREAM_ID

object Constants {
    const val MAIN_SCREEN = "MainScreen"
    const val STREAMER_SCREEN = "StreamerScreen"
    fun getViewerScreen(streamId: String) = "ViewerScreen/$streamId"
    val REMOTE_SOCKET_URL = "ws://164.92.142.251:3333/app/$STREAM_ID?direction=send"

}