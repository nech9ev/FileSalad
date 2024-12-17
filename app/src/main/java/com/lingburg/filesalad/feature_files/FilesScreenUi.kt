package com.lingburg.filesalad.feature_files

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class FilesScreenUi(
    val words: ImmutableList<WordUi> = persistentListOf(),
    val inProgress: Boolean,
) {

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
            inProgress = false,
        )
    }
}

@Immutable
data class WordUi(
    val label: String = "",
    val text: String = "",
)
