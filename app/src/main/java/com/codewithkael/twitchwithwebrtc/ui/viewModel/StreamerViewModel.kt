package com.codewithkael.twitchwithwebrtc.ui.viewModel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.codewithkael.twitchwithwebrtc.remote.socket.RemoteSocketClient
import com.codewithkael.twitchwithwebrtc.utils.MessageModel
import com.codewithkael.twitchwithwebrtc.webrtc.WebRTCFactory
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private var localSurface : SurfaceViewRenderer?=null

    //states

    fun init(){
       socketClient.init(this)
    }


    fun onLocalSurfaceReady(localSurface: SurfaceViewRenderer) {
        webRTCFactory.prepareLocalStream(localSurface, object : WebRTCFactory.LocalStreamListener {
            override fun onLocalStreamReady(mediaStream: MediaStream) {
                mediaStream.videoTracks[0]?.addSink(localSurface)
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        localSurface?.release()
        localSurface = null
    }

    override fun onRemoteSocketClientOpened() {

    }

    override fun onRemoteSocketClientClosed() {
    }

    override fun onRemoteSocketClientConnectionError(e: Exception?) {
    }

    override fun onRemoteSocketClientNewMessage(message: MessageModel) {
    }
}