package com.codewithkael.twitchwithwebrtc.webrtc

import android.content.Context
import com.codewithkael.twitchwithwebrtc.utils.MyApplication.Companion.STREAM_ID
import com.google.gson.Gson
import org.webrtc.AudioTrack
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraVideoCapturer
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnection.IceServer
import org.webrtc.PeerConnectionFactory
import org.webrtc.SurfaceTextureHelper
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoTrack
import javax.inject.Inject


class WebRTCFactory @Inject constructor(
    private val context: Context,
    private val gson: Gson,
) {
    private val peerConnectionFactory by lazy { createPeerConnectionFactory() }
    private val eglBaseContext = EglBase.create().eglBaseContext
    private val iceServer = listOf<IceServer>(
        IceServer.builder("stun:stun1.l.google.com:19302").createIceServer(),
        IceServer.builder("stun:178.33.166.29:3478").createIceServer(),
        IceServer.builder("stun:37.139.120.14:3478").createIceServer(),
        IceServer.builder("stun:194.149.74.157:3478").createIceServer(),
        IceServer.builder("stun:193.22.119.20:3478").createIceServer(),
        IceServer.builder("stun:stun.relay.metered.ca:80").createIceServer(),
        IceServer.builder("turn:global.relay.metered.ca:80")
            .setUsername("0da9dc3f3ca0b8aef7388ca9")
            .setPassword("KuuHVTmXU80Q1WMO")
            .createIceServer(),
        IceServer.builder("turn:global.relay.metered.ca:80?transport=tcp")
            .setUsername("0da9dc3f3ca0b8aef7388ca9")
            .setPassword("KuuHVTmXU80Q1WMO")
            .createIceServer(),
        IceServer.builder("turn:global.relay.metered.ca:443")
            .setUsername("0da9dc3f3ca0b8aef7388ca9")
            .setPassword("KuuHVTmXU80Q1WMO")
            .createIceServer(),
        IceServer.builder("turns:global.relay.metered.ca:443?transport=tcp")
            .setUsername("0da9dc3f3ca0b8aef7388ca9")
            .setPassword("KuuHVTmXU80Q1WMO")
            .createIceServer()
    )
    private var videoCapturer: CameraVideoCapturer? = null

    private val localVideoSource by lazy { peerConnectionFactory.createVideoSource(false) }
    private val localAudioSource by lazy { peerConnectionFactory.createAudioSource(MediaConstraints()) }

    private val streamId = "${STREAM_ID}_stream"
    private var localAudioTrack: AudioTrack? = null
    private var localVideoTrack: VideoTrack? = null
    private var localStream: MediaStream? = null
    private var localStreamListener: LocalStreamListener? = null

    init {
        initPeerConnectionFactory(context)
    }

    private fun prepareLocalStream(
        view: SurfaceViewRenderer,
        localStreamListener: LocalStreamListener
    ) {
        this.localStreamListener = localStreamListener
        initSurfaceView(view)
        startLocalVideo(view)
    }

    fun initSurfaceView(view: SurfaceViewRenderer) {
        view.run {
            setMirror(false)
            setEnableHardwareScaler(true)
            init(eglBaseContext, null)
        }
    }

    private fun startLocalVideo(surface: SurfaceViewRenderer) {
        val surfaceTextureHelper =
            SurfaceTextureHelper.create(Thread.currentThread().name, eglBaseContext)
        videoCapturer = getVideoCapturer()
        videoCapturer?.initialize(
            surfaceTextureHelper,
            surface.context, localVideoSource.capturerObserver
        )
        videoCapturer?.startCapture(720, 480, 10)
        localVideoTrack =
            peerConnectionFactory.createVideoTrack(streamId + "_video", localVideoSource)
        localVideoTrack?.addSink(surface)
        localAudioTrack =
            peerConnectionFactory.createAudioTrack(streamId + "_audio", localAudioSource)
        localStream = peerConnectionFactory.createLocalMediaStream(streamId)
        localStream?.addTrack(localAudioTrack)
        localStream?.addTrack(localVideoTrack)
        localStreamListener?.onLocalStreamReady(localStream!!)

    }

    private fun getVideoCapturer(): CameraVideoCapturer {
        return Camera2Enumerator(context).run {
            deviceNames.find {
                isFrontFacing(it)
            }?.let {
                createCapturer(it, null)
            } ?: throw IllegalStateException()
        }
    }

    private fun initPeerConnectionFactory(application: Context) {
        val options = PeerConnectionFactory.InitializationOptions.builder(application)
            .setEnableInternalTracer(true).setFieldTrials("WebRTC-H264HighProfile/Enabled/")
            .createInitializationOptions()
        PeerConnectionFactory.initialize(options)
    }

    private fun createPeerConnectionFactory(): PeerConnectionFactory {
        return PeerConnectionFactory.builder().setVideoDecoderFactory(
            DefaultVideoDecoderFactory(eglBaseContext)
        ).setVideoEncoderFactory(
            DefaultVideoEncoderFactory(
                eglBaseContext, true, true
            )
        ).setOptions(PeerConnectionFactory.Options().apply {
            disableEncryption = false
            disableNetworkMonitor = false
        }).createPeerConnectionFactory()
    }


    fun onDestroy() {
        runCatching {
            localStreamListener = null
            localStream?.dispose()
            videoCapturer?.stopCapture()
            videoCapturer?.dispose()
        }
    }

    fun createStreamerClient(
        observer: PeerConnection.Observer,
        listener: StreamerRTCClientImpl.TransferStreamerDataToServerListener
    ): RTCClient? {
        val connection = peerConnectionFactory.createPeerConnection(
            PeerConnection.RTCConfiguration(iceServer), observer
        )
        connection?.addStream(localStream)
        return connection?.let { StreamerRTCClientImpl(it, listener) }
    }


}