package com.codewithkael.twitchwithwebrtc.webrtc
import org.webrtc.IceCandidate
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription

interface RTCClient {

    val peerConnection :PeerConnection
    fun onDestroy()
    fun answer()
    fun onRemoteSessionReceived(sessionDescription: SessionDescription)
    fun onIceCandidateReceived(iceCandidate: IceCandidate)
    fun onLocalIceCandidateGenerated(iceCandidate: IceCandidate)

}