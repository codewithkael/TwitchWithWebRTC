package com.codewithkael.twitchwithwebrtc.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.codewithkael.twitchwithwebrtc.ui.screens.MainScreen
import com.codewithkael.twitchwithwebrtc.ui.screens.StreamerScreen
import com.codewithkael.twitchwithwebrtc.ui.screens.ViewerScreen
import com.codewithkael.twitchwithwebrtc.utils.Constants.MAIN_SCREEN
import com.codewithkael.twitchwithwebrtc.utils.Constants.STREAMER_SCREEN

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = MAIN_SCREEN) {
        composable(MAIN_SCREEN) {
            MainScreen(navController = navController)
        }
        composable(STREAMER_SCREEN) {
            StreamerScreen()
        }
        composable(
            "ViewerScreen/{streamId}",
            arguments = listOf(navArgument("streamId") { type = NavType.StringType })
        ) {
            ViewerScreen(
                streamId = it.arguments?.getString("streamId")
            )
        }
    }
}