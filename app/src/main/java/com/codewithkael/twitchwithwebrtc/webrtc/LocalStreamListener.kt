package com.codewithkael.twitchwithwebrtc.webrtc

import org.webrtc.MediaStream

interface LocalStreamListener {
    fun onLocalStreamReady(mediaStream: MediaStream)
}