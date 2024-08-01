package com.codewithkael.twitchwithwebrtc.webrtc

import com.codewithkael.twitchwithwebrtc.utils.MessageModel
import com.codewithkael.twitchwithwebrtc.utils.MyIceCandidates
import com.codewithkael.twitchwithwebrtc.utils.MySessionDescription
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription

class RTCClientImpl(
    connection: PeerConnection,
    private val transferListener: TransferStreamerDataToServerListener
) : RTCClient {

    private var remoteMessageModel = MessageModel(command = "answer")
    private fun resetLocalOffer(id:Int) {
        remoteMessageModel = MessageModel(command = "answer", id = id)
    }

    private val mediaConstraint = MediaConstraints().apply {
        mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
    }

    override val peerConnection: PeerConnection = connection

    override fun answer(id:Int) {
        resetLocalOffer(id)
        peerConnection.createAnswer(object : MySdpObserver() {
            override fun onCreateSuccess(desc: SessionDescription?) {
                super.onCreateSuccess(desc)
                peerConnection.setLocalDescription(object : MySdpObserver() {
                    override fun onSetSuccess() {
                        super.onSetSuccess()
                        remoteMessageModel.sdp = MySessionDescription(
                            type = "answer",
                            sdp = desc?.description.toString()
                        )
                        CoroutineScope(Dispatchers.IO).launch {
                            //wait for gathering the candidates
                            delay(3000)
                            transferListener.onTransferEventToSocket(remoteMessageModel)
                        }
                    }
                }, desc)
            }
        }, mediaConstraint)
    }



    override fun onRemoteSessionReceived(sessionDescription: SessionDescription) {
        peerConnection.setRemoteDescription(MySdpObserver(),sessionDescription)
    }

    override fun onIceCandidateReceived(iceCandidate: IceCandidate) {
        peerConnection.addIceCandidate(iceCandidate)

    }


    override fun onDestroy() {
        peerConnection.close()
    }

    override fun onLocalIceCandidateGenerated(iceCandidate: IceCandidate) {
        peerConnection.addIceCandidate(iceCandidate)
        remoteMessageModel.candidates.add(
            MyIceCandidates(
                candidate = iceCandidate.sdp,
                sdpMLineIndex = iceCandidate.sdpMLineIndex,
                sdpMid = iceCandidate.sdpMid
            )
        )
    }

    interface TransferStreamerDataToServerListener {
        fun onTransferEventToSocket(data: MessageModel)
    }
}