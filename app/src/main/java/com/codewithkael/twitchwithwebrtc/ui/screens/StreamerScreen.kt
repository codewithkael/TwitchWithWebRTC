package com.codewithkael.twitchwithwebrtc.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.codewithkael.twitchwithwebrtc.ui.components.SurfaceViewRendererComposable
import com.codewithkael.twitchwithwebrtc.ui.viewModel.StreamerViewModel
import com.codewithkael.twitchwithwebrtc.utils.MyApplication.Companion.STREAM_ID

@Composable
fun StreamerScreen(navController: NavController) {
    val viewModel: StreamerViewModel = hiltViewModel()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "ID: $STREAM_ID", modifier = Modifier.weight(1f).padding(start = 20.dp, top = 30.dp, bottom = 10.dp))
        SurfaceViewRendererComposable(modifier = Modifier.weight(10f)) { localSurface ->
           viewModel.onLocalSurfaceReady(localSurface)
        }
    }
}