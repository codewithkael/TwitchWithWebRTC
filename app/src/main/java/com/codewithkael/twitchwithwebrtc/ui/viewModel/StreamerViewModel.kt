package com.codewithkael.twitchwithwebrtc.ui.viewModel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.codewithkael.twitchwithwebrtc.remote.socket.RemoteSocketClient
import com.codewithkael.twitchwithwebrtc.utils.Constants.REMOTE_SOCKET_URL
import com.codewithkael.twitchwithwebrtc.utils.MessageModel
import com.codewithkael.twitchwithwebrtc.utils.toWebrtcCandidate
import com.codewithkael.twitchwithwebrtc.utils.toWebrtcSessionDescription
import com.codewithkael.twitchwithwebrtc.webrtc.MyPeerObserver
import com.codewithkael.twitchwithwebrtc.webrtc.RTCClient
import com.codewithkael.twitchwithwebrtc.webrtc.RTCClientImpl
import com.codewithkael.twitchwithwebrtc.webrtc.WebRTCFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.SurfaceViewRenderer
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class StreamerViewModel @Inject constructor(
    private val webRTCFactory: WebRTCFactory,
    private val socketClient: RemoteSocketClient
) : ViewModel(), RemoteSocketClient.RemoteSocketServerListener {

    //variables
    private var localSurface: SurfaceViewRenderer? = null
    private var remoteRTCClient: RTCClient? = null

    //states

    private fun initSocketClient() {
        socketClient.init(REMOTE_SOCKET_URL, this)
    }


    private fun initRemoteRTCClient() {
        runCatching {
            remoteRTCClient?.onDestroy()
        }
        remoteRTCClient = null
        remoteRTCClient = webRTCFactory.createRTCClient(
            object : MyPeerObserver() {
                override fun onIceCandidate(p0: IceCandidate?) {
                    super.onIceCandidate(p0)
                    p0?.let { remoteRTCClient?.onLocalIceCandidateGenerated(it) }
                }
            },
            object : RTCClientImpl.TransferStreamerDataToServerListener {
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
        webRTCFactory.onDestroy()
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
            initRemoteRTCClient()
            message.sdp?.let {
                remoteRTCClient?.onRemoteSessionReceived(it.toWebrtcSessionDescription())
            }
            message.candidates.forEach { ice ->
                remoteRTCClient?.onIceCandidateReceived(ice.toWebrtcCandidate())
            }
            remoteRTCClient?.answer(message.id!!)
        }
    }
}