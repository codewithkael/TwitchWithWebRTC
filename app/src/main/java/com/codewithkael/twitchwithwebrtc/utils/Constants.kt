package com.codewithkael.twitchwithwebrtc.utils

import com.codewithkael.twitchwithwebrtc.utils.MyApplication.Companion.STREAM_ID

object Constants {
    const val MAIN_SCREEN = "MainScreen"
    const val STREAMER_SCREEN = "StreamerScreen"
    fun getViewerScreen(streamId: String) = "ViewerScreen/$streamId"

    private const val BASE_URL = "128.140.38.246"
    val REMOTE_SOCKET_URL = "ws://$BASE_URL:3333/app/$STREAM_ID?direction=send"
    fun getStreamPath(id:String)="ws://$BASE_URL:3333/app/$id"
}