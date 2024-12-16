package com.lingburg.filesalad

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController


@Composable
fun rememberFileSaladAppState(
    navController: NavHostController = rememberNavController(),
): FileSaladAppState {
    return remember(
        navController,
    ) {
        FileSaladAppState(
            navController = navController,
        )
    }
}

@Stable
class FileSaladAppState(
    val navController: NavHostController,
)
