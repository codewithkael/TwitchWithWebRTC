package com.codewithkael.twitchwithwebrtc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.codewithkael.twitchwithwebrtc.ui.components.SurfaceViewRendererComposable
import com.codewithkael.twitchwithwebrtc.ui.viewModel.StreamerViewModel
import com.codewithkael.twitchwithwebrtc.utils.MyApplication.Companion.STREAM_ID

@Composable
fun StreamerScreen() {
    val viewModel: StreamerViewModel = hiltViewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp), // Padding around the entire column
        verticalArrangement = Arrangement.spacedBy(16.dp) // Space between elements
    ) {
        // Display the stream ID with enhanced text styling
        StreamIdDisplay(streamId = STREAM_ID)

        // Display the local surface view in a card with a border
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Takes most of the screen
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(12.dp)
                ),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            SurfaceViewRendererComposable(modifier = Modifier.fillMaxSize()) { localSurface ->
                viewModel.onLocalSurfaceReady(localSurface)
            }
        }
    }
}

@Composable
fun StreamIdDisplay(streamId: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Stream ID:",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp
                )
            )
            Text(
                text = streamId,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            )
        }
    }
}
