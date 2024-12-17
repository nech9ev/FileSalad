package com.lingburg.filesalad

import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import timber.log.Timber
import java.io.File


class DownloadFileListener : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent ?: return
        context ?: return
        if (intent.action != DownloadManager.ACTION_DOWNLOAD_COMPLETE) return

        val fileId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, NOT_FOUND_ID)
        if (fileId == NOT_FOUND_ID) return

        val downloadManager = context.getSystemService(DownloadManager::class.java)
        val fileUri = getFileUriFromDownloadManager(downloadManager, fileId) ?: return
        val file = getFileFromFileUri(fileUri) ?: return

        val uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, file)
        Timber.i("DownloadFileListener FileProvider uri:$uri")
        try {
            context.startActivity(Intent().apply {
                action = Intent.ACTION_VIEW
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setDataAndType(uri, context.contentResolver.getType(uri))
            })
        } catch (_: ActivityNotFoundException) {
            Timber.i("DownloadFileListener ActivityNotFoundException")
        }
    }

    private fun getFileUriFromDownloadManager(
        downloadManager: DownloadManager, downloadId: Long
    ): Uri? {
        val query = DownloadManager.Query()
        query.setFilterById(downloadId)
        val cursor = downloadManager.query(query)

        if (cursor.moveToFirst()) {
            val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(statusIndex)) {
                val uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                val uriString = cursor.getString(uriIndex)
                if (uriString != null) {
                    return Uri.parse(uriString)
                }
            } else if (DownloadManager.STATUS_FAILED == cursor.getInt(statusIndex)) {
                val reasonIndex = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
                val reason = cursor.getInt(reasonIndex)
                Timber.e("DownloadFileListener Download Error: $reason")
            }
        }
        cursor.close()
        return null
    }

    private fun getFileFromFileUri(uri: Uri): File? {
        return try {
            uri.path?.let { path -> File(path) }
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        }
    }


    companion object {

        private const val NOT_FOUND_ID = -1L
    }
}
