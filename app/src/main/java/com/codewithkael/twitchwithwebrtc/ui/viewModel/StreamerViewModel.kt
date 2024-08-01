package com.codewithkael.twitchwithwebrtc.ui.viewModel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import com.codewithkael.twitchwithwebrtc.remote.socket.RemoteSocketClient
import com.codewithkael.twitchwithwebrtc.utils.MessageModel
import com.codewithkael.twitchwithwebrtc.utils.MyApplication.Companion.REMOTE_CALL_ID
import com.codewithkael.twitchwithwebrtc.utils.toWebrtcCandidate
import com.codewithkael.twitchwithwebrtc.utils.toWebrtcSessionDescription
import com.codewithkael.twitchwithwebrtc.webrtc.MyPeerObserver
import com.codewithkael.twitchwithwebrtc.webrtc.RTCClient
import com.codewithkael.twitchwithwebrtc.webrtc.StreamerRTCClientImpl
import com.codewithkael.twitchwithwebrtc.webrtc.WebRTCFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.SurfaceViewRenderer
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class StreamerViewModel @Inject constructor(
    private val webRTCFactory: WebRTCFactory,
    private val socketClient: RemoteSocketClient
) : ViewModel(), RemoteSocketClient.RemoteSocketServerListener {

    //variables
    private val TAG = "StreamerViewModel"
    private var localSurface : SurfaceViewRenderer?=null
    private var remoteRTCClient: RTCClient? = null

    //states

    private fun initSocketClient(){
       socketClient.init(this)
    }


    private fun initRemoteRTCClient() {
        runCatching {
            remoteRTCClient?.onDestroy()
        }
        remoteRTCClient = null
        remoteRTCClient = webRTCFactory.createStreamerClient(
            object : MyPeerObserver() {
                override fun onIceCandidate(p0: IceCandidate?) {
                    super.onIceCandidate(p0)
                    p0?.let { remoteRTCClient?.onLocalIceCandidateGenerated(it) }
                }

                override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
                    super.onConnectionChange(newState)
                    if (newState == PeerConnection.PeerConnectionState.DISCONNECTED ||
                        newState == PeerConnection.PeerConnectionState.FAILED
                    ) {
                        onRemoteSocketClientOpened()
                    }
                    if (newState == PeerConnection.PeerConnectionState.CONNECTED) {
                        //update status here

                    }
                    Log.d(TAG, "onConnectionChange: remote state $newState")
                }
            },
            object : StreamerRTCClientImpl.TransferStreamerDataToServerListener {
                override fun onTransferEventToSocket(data: MessageModel) {
                    socketClient.sendDataToHost(data)
                }
            }
        )
    }


    fun onLocalSurfaceReady(localSurface: SurfaceViewRenderer) {
        webRTCFactory.prepareLocalStream(localSurface, object : WebRTCFactory.LocalStreamListener {
            override fun onLocalStreamReady(mediaStream: MediaStream) {
                mediaStream.videoTracks[0]?.let {
                    it.addSink(localSurface)
                    initSocketClient()
                }
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        localSurface?.release()
        localSurface = null
    }

    override fun onRemoteSocketClientOpened() {
        socketClient.sendDataToHost(
            MessageModel(command = "request_offer")
        )
    }

    override fun onRemoteSocketClientClosed() {
    }

    override fun onRemoteSocketClientConnectionError(e: Exception?) {
    }

    override fun onRemoteSocketClientNewMessage(message: MessageModel) {
        if (message.command == "offer") {
            REMOTE_CALL_ID = message.id
            initRemoteRTCClient()
            message.sdp?.let {
                remoteRTCClient?.onRemoteSessionReceived(it.toWebrtcSessionDescription())
            }
            message.candidates.forEach { ice ->
                remoteRTCClient?.onIceCandidateReceived(ice.toWebrtcCandidate())
            }
            remoteRTCClient?.answer()
        }
    }
}