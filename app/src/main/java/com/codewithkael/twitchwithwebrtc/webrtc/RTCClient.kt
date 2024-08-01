package com.codewithkael.twitchwithwebrtc.webrtc
import org.webrtc.IceCandidate
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription

interface RTCClient {

    val peerConnection :PeerConnection
    fun onDestroy()
    fun answer(id:Int)
    fun onRemoteSessionReceived(sessionDescription: SessionDescription)
    fun onIceCandidateReceived(iceCandidate: IceCandidate)
    fun onLocalIceCandidateGenerated(iceCandidate: IceCandidate)

}