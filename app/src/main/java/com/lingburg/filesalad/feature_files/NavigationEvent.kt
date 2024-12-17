package com.lingburg.filesalad.feature_files

sealed interface NavigationEvent {

    data object Empty : NavigationEvent

    class ShareWords(
        val text: String,
    ) : NavigationEvent
}
