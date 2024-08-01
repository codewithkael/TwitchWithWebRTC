package com.codewithkael.twitchwithwebrtc.ui.viewModel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.codewithkael.twitchwithwebrtc.remote.socket.RemoteSocketClient
import com.codewithkael.twitchwithwebrtc.utils.Constants.getStreamPath
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
class ViewerViewModel @Inject constructor(
    private val socketClient: RemoteSocketClient,
    private val webRTCFactory: WebRTCFactory
) : ViewModel(), RemoteSocketClient.RemoteSocketServerListener {

    //variables
    private var remoteRTCClient: RTCClient? = null
    private var remoteSurface: SurfaceViewRenderer? = null

    fun init(streamId: String) {
        initSocketClient(streamId)
    }

    private fun initSocketClient(streamId: String) {
        socketClient.init(getStreamPath(streamId), this@ViewerViewModel)
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

                override fun onAddStream(p0: MediaStream?) {
                    super.onAddStream(p0)
                    p0?.videoTracks?.get(0)?.addSink(remoteSurface)
                }
            },
            object : RTCClientImpl.TransferStreamerDataToServerListener {
                override fun onTransferEventToSocket(data: MessageModel) {
                    socketClient.sendDataToHost(data)
                }
            }
        )
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

    fun onRemoteSurfaceReady(remoteSurface: SurfaceViewRenderer) {
        this.remoteSurface = remoteSurface
        webRTCFactory.initSurfaceView(remoteSurface)
    }

    override fun onCleared() {
        super.onCleared()
        remoteSurface?.release()
        remoteSurface = null
        webRTCFactory.onDestroy()
    }
}