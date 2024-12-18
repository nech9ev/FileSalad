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
                val downloadLink = interactor.getDownloadLink(words, uiState.value.baseUrl)
                download(
                    fileName = downloadLink.fileName,
                )
            },
            catchBlock = { throwable ->
                snackbarState.tryEmit(
                    SnackbarState.Success(
                        text = "Error while downloading. Try again please"
                    )
                )
                Timber.e(throwable)
            },
            finalBlock = {
                uiState.update { ui ->
                    ui.copy(
                        downloadProgress = false,
                    )
                }
            }
        )
    }

    fun onDocumentSelected(uri: Uri) {
        Timber.e(uri.toString())
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
                val file = File(path)
                Timber.e(file.readText(charset = Charsets.UTF_8))
                val words = interactor.uploadFile(
                    file = file,
                    name = fileName,
                    baseUrl = uiState.value.baseUrl,
                ).key.split("_")
                Timber.e(words.toString())
                navigationEvent.emit(
                    NavigationEvent.ShareWords(
                        text = words.joinToString(separator = " "),
                    )
                )
            },
            catchBlock = { throwable ->
                Timber.e(throwable)
                snackbarState.tryEmit(
                    SnackbarState.Error(
                        text = "Error while uploading. Try again please"
                    )
                )
            },
            finalBlock = {
                uiState.update { ui ->
                    ui.copy(
                        uploadProgress = false,
                    )
                }
            }
        )
    }

    private fun download(
        fileName: String,
    ) {
        val downloadManager = applicationContext.getSystemService(DownloadManager::class.java)
        val fileLink = "${uiState.value.baseUrl}/download?param=$fileName"
        Timber.e("fileLink")
        Timber.e(fileLink)
        val request = DownloadManager.Request(fileLink.toUri()).apply {
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

    fun onBaseUrlChange(text: String) {
        uiState.update { ui ->
            ui.copy(baseUrl = text)
        }
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
