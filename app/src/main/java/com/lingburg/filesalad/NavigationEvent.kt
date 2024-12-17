package com.lingburg.filesalad

sealed interface NavigationEvent {

    data object Empty : NavigationEvent

    class ShareWords(
        val text: String,
    ) : NavigationEvent
}
