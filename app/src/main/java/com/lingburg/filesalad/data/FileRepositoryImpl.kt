package com.lingburg.filesalad.data

import com.lingburg.filesalad.data.datasource.FileDataSource
import com.lingburg.filesalad.data.datasource.FileDownloadResponse
import com.lingburg.filesalad.data.datasource.FileUploadResponse
import com.lingburg.filesalad.domain.FileRepository
import java.io.File
import javax.inject.Inject

class FileRepositoryImpl @Inject constructor(
    private val dataSource: FileDataSource,
) : FileRepository {

    override suspend fun getDownloadLink(
        words: List<String>,
    ): FileDownloadResponse {
        return dataSource.fetchDownloadLink(words)
    }

    override suspend fun uploadFile(file: File, name: String): FileUploadResponse {
        return dataSource.uploadFile(file, name)
    }
}
