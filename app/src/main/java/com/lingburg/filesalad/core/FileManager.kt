package com.lingburg.filesalad.core

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.FileUtils.copy
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object FileManager {

    fun getFileName(uri: Uri, contentResolver: ContentResolver): String? {
        var result: String? = null

        if (uri.scheme == "content") {
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (cursor != null && cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (columnIndex == -1) return null
                    result = cursor.getString(columnIndex)
                }
            }
        }

        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/') ?: 0
            if (cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result
    }

    @Throws(IOException::class)
    fun getFilePathFromUri(uri: Uri?, contentResolver: ContentResolver, externalCacheDir: File?): Uri? {
        if (uri != null) {
            val fileName = getFileName(uri, contentResolver) ?: return null
            val file = File(externalCacheDir, fileName)
            file.createNewFile()
            FileOutputStream(file).use { outputStream ->
                contentResolver.openInputStream(uri).use { inputStream ->
                    inputStream?.let { copy(it, outputStream) }
                    outputStream.flush()
                }
            }
            return Uri.fromFile(file)
        }
        return null
    }
}
