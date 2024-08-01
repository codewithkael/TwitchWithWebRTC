package com.codewithkael.twitchwithwebrtc.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.codewithkael.twitchwithwebrtc.ui.navigation.MainNavigation
import com.codewithkael.twitchwithwebrtc.ui.theme.TwitchWithWebRTCTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TwitchWithWebRTCTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainNavigation()
                }
            }
        }
    }
}
