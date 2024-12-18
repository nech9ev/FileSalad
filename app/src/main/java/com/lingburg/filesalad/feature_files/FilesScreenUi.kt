package com.lingburg.filesalad.feature_files

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class FilesScreenUi(
    val words: ImmutableList<WordUi> = persistentListOf(),
    val downloadProgress: Boolean,
    val uploadProgress: Boolean,
    val baseUrl: String = "http://87.228.37.6:8080",
) {

    val inProgress = downloadProgress || uploadProgress

    companion object {

        fun empty() = FilesScreenUi(
            words = persistentListOf(
                WordUi(
                    label = "First word",
                ),
                WordUi(
                    label = "Second word",
                ),
                WordUi(
                    label = "Third word",
                )
            ),
            downloadProgress = false,
            uploadProgress = false,
        )
    }
}

@Immutable
data class WordUi(
    val label: String = "",
    val text: String = "",
)
