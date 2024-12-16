package com.lingburg.filesalad

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lingburg.filesalad.feature_download.DownloadScreen


@Composable
fun FileSaladNavHost(
    appState: FileSaladAppState,
    modifier: Modifier = Modifier,
) {
    NavHost(
        modifier = modifier,
        navController = appState.navController,
        startDestination = DownloadFileScreen,
    ) {
        composable<Main> {

        }
        composable<UploadFile> {

        }
        composable<DownloadFileScreen> {
            DownloadScreen(
                appState = appState,
            )
        }
    }
}
