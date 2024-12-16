package com.lingburg.filesalad.feature_download

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DownloadScreenViewModel @Inject constructor(

) : ViewModel() {

    val uiState: MutableStateFlow<DownloadScreenUi> = MutableStateFlow(DownloadScreenUi.empty())

    fun onWordTextChanged(updatedIndex: Int, text: String) {
        uiState.update { ui ->
            ui.copy(
                words = ui.words.mapIndexed { index, wordUi ->
                    when (index) {
                        updatedIndex -> wordUi.copy(
                            text = text,
                        )
                        else -> wordUi
                    }
                }.toImmutableList()
            )
        }
    }

    fun onDownloadClick() {

    }

    fun onBackClick() {

    }
}
