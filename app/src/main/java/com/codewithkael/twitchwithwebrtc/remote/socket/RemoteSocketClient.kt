package com.codewithkael.twitchwithwebrtc.remote.socket

import android.util.Log
import com.codewithkael.twitchwithwebrtc.utils.Constants.REMOTE_SOCKET_URL
import com.codewithkael.twitchwithwebrtc.utils.MessageModel
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import javax.inject.Inject

class RemoteSocketClient @Inject constructor(
    private val gson: Gson
) {
    private var socketServer: WebSocketClient? = null
    private val TAG = "RemoteSocketClient"
    fun init(
        listener: RemoteSocketServerListener,
    ) {
        if (socketServer == null) {
            socketServer = object :
                WebSocketClient(URI(REMOTE_SOCKET_URL)) {
                override fun onOpen(handshakedata: ServerHandshake?) {
                    Log.d(TAG, "onOpen: ")
                    listener.onRemoteSocketClientOpened()
                }

                override fun onMessage(message: String?) {
                    Log.d(TAG, "onMessage: $message")
                    runCatching {
                        gson.fromJson(message.toString(), MessageModel::class.java)
                    }.onSuccess {
                        listener.onRemoteSocketClientNewMessage(it)
                    }.onFailure {
                        Log.d(TAG, "onMessage: ${it.message}")
                    }
                }

                override fun onClose(code: Int, reason: String?, remote: Boolean) {
                    Log.d(TAG, "onClose: $reason")
                    listener.onRemoteSocketClientClosed()
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(5000)
                        init(listener)
                    }
                }

                override fun onError(ex: Exception?) {
                    Log.d(TAG, "onError1: ${ex?.message}")
                    listener.onRemoteSocketClientConnectionError(ex)
                }
            }.apply {
                connect()
            }
        }
    }

    fun sendDataToHost(data: Any) {
        Log.d(TAG, "sendDataToHost: $data")
        runCatching {
            socketServer?.send(gson.toJson(data))
        }
    }

    interface RemoteSocketServerListener {
        fun onRemoteSocketClientOpened()
        fun onRemoteSocketClientClosed()
        fun onRemoteSocketClientConnectionError(e: Exception?)
        fun onRemoteSocketClientNewMessage(message: MessageModel)
    }
}