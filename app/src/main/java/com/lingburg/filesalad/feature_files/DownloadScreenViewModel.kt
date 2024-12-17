package com.lingburg.filesalad.feature_files

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingburg.filesalad.core.FileManager
import com.lingburg.filesalad.domain.FileInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


@HiltViewModel
class DownloadScreenViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val interactor: FileInteractor,
) : ViewModel() {

    val uiState: MutableStateFlow<FilesScreenUi> = MutableStateFlow(FilesScreenUi.empty())
    val navigationEvent: MutableSharedFlow<NavigationEvent> = MutableSharedFlow()
    val snackbarState: MutableSharedFlow<SnackbarState> = MutableSharedFlow()

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

        viewModelScope.launchCatching(
            tryBlock = {
                uiState.update { ui ->
                    ui.copy(
                        downloadProgress = true,
                    )
                }
                val downloadLink = interactor.getDownloadLink(words)
                download(
                    link = downloadLink.fileLink,
                    fileName = downloadLink.fileName,
                )
                snackbarState.emit(
                    SnackbarState.Success(
                        text = "File downloaded"
                    )
                )
            },
            finalBlock = {
                uiState.update { ui ->
                    ui.copy(
                        downloadProgress = false,
                    )
                }
                snackbarState.tryEmit(
                    SnackbarState.Success(
                        text = "Error while downloading. Try again please"
                    )
                )
            }
        )
    }

    fun onDocumentSelected(uri: Uri) {
        val fileName = FileManager.getFileName(uri, applicationContext.contentResolver)
        val path = FileManager.getFilePathFromUri(
            uri,
            applicationContext.contentResolver,
            applicationContext.externalCacheDir
        )?.path
        fileName ?: return
        path ?: return

        viewModelScope.launchCatching(
            tryBlock = {
                uiState.update { ui ->
                    ui.copy(
                        uploadProgress = true,
                    )
                }
                val words = interactor.uploadFile(
                    file = File(path),
                    name = fileName
                ).words
                Timber.e(words.toString())
                navigationEvent.emit(
                    NavigationEvent.ShareWords(
                        text = words.joinToString(separator = " "),
                    )
                )
            },
            finalBlock = {
                uiState.update { ui ->
                    ui.copy(
                        uploadProgress = false,
                    )
                }
                snackbarState.tryEmit(
                    SnackbarState.Success(
                        text = "Error while uploading. Try again please"
                    )
                )
            }
        )
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


inline fun CoroutineScope.launchCatching(
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline catchBlock: (Throwable) -> Unit = {},
    crossinline finalBlock: () -> Unit = {},
    crossinline tryBlock: suspend () -> Unit
): Job = launch(context) {
    try {
        tryBlock()
    } catch (expected: CancellationException) {
        throw expected
    } catch (e: Throwable) {
        catchBlock(e)
    } finally {
        finalBlock()
    }
}
