package com.lingburg.filesalad

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lingburg.filesalad.feature_files.FilesScreen


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
        composable<DownloadFileScreen> {
            FilesScreen(
                appState = appState,
            )
        }
    }
}
