package com.lingburg.filesalad.domain

import com.lingburg.filesalad.data.datasource.FileDownloadResponse
import com.lingburg.filesalad.data.datasource.FileUploadResponse
import java.io.File
import javax.inject.Inject

class FileInteractor @Inject constructor(
    private val repository: FileRepository,
) {

    suspend fun getDownloadLink(
        words: List<String>,
    ): FileDownloadResponse {
        return repository.getDownloadLink(words)
    }

    suspend fun uploadFile(
        file: File,
        name: String,
    ): FileUploadResponse {
        return repository.uploadFile(file, name)
    }
}
