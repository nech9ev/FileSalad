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
        baseUrl: String,
    ): FileDownloadResponse {
        return repository.getDownloadLink(words, baseUrl)
    }

    suspend fun uploadFile(
        file: File,
        name: String,
        baseUrl: String,
    ): FileUploadResponse {
        return repository.uploadFile(file, name, baseUrl)
    }
}
