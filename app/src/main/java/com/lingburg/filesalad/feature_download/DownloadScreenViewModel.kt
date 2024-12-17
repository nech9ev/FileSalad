package com.lingburg.filesalad.feature_download

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingburg.filesalad.DownloadFileListener
import com.lingburg.filesalad.domain.DownloadInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DownloadScreenViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val interactor: DownloadInteractor,
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
        val words = uiState.value.words.map { wordUi ->
            wordUi.text
        }
        if (words.any { word -> word.isBlank() }) return

        viewModelScope.launch {
            val downloadLink = interactor.getDownloadLink(words)
            download(
                link = downloadLink.fileLink,
                fileName = downloadLink.fileName,
            )
        }
    }

    fun onBackClick() {

    }

    private fun download(
        link: String,
        fileName: String,
    ) {
        val downloadManager = applicationContext.getSystemService(DownloadManager::class.java)
        val request = DownloadManager.Request(link.toUri()).apply {
            setTitle(fileName)
            setDescription("Downloading file with FileSalad")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            setRequiresCharging(false)
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
            setMimeType(getMimeType(fileName))
        }
        downloadManager.enqueue(request)
    }

    private fun getMimeType(path: String?): String? {
        path ?: return null
        val extension = MimeTypeMap.getFileExtensionFromUrl(path)
        return if (extension != null) {
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        } else null
    }
}
