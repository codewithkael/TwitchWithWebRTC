package com.codewithkael.twitchwithwebrtc.utils

import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

data class MessageModel(
    var command: String? = null,
    var id: Int? = null,
    var sdp: MySessionDescription? = null,
    var candidates: MutableList<MyIceCandidates> = mutableListOf()
)

data class MySessionDescription(
    val type: String,
    val sdp: String
)

fun MySessionDescription.toWebrtcSessionDescription(): SessionDescription {
    return SessionDescription(
        if (type == "offer") SessionDescription.Type.OFFER else SessionDescription.Type.ANSWER,
        sdp
    )
}

data class MyIceCandidates(
    val candidate: String,
    val sdpMLineIndex: Int,
    val sdpMid: String? = ""
)

fun MyIceCandidates.toWebrtcCandidate(): IceCandidate {
    return IceCandidate(
        sdpMid ?: "",
        sdpMLineIndex,
        candidate
    )
}