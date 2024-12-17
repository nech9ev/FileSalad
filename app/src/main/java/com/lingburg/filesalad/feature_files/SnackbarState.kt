package com.lingburg.filesalad.feature_files

sealed interface SnackbarState {

    data object Empty : SnackbarState

    class Error(
        val text: String,
    ) : SnackbarState

    class Success(
        val text: String,
    ) : SnackbarState
}
