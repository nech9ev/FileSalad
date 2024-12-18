package com.lingburg.filesalad.domain

import com.lingburg.filesalad.data.datasource.FileDownloadResponse
import com.lingburg.filesalad.data.datasource.FileUploadResponse
import java.io.File

interface FileRepository {

    suspend fun getDownloadLink(
        words: List<String>,
        baseUrl: String,
    ): FileDownloadResponse

    suspend fun uploadFile(
        file: File,
        name: String,
        baseUrl: String,
    ): FileUploadResponse
}
