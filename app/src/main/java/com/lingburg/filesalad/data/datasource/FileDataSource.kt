package com.lingburg.filesalad.data.datasource

import io.ktor.client.HttpClient
import kotlinx.coroutines.delay
import java.io.File
import javax.inject.Inject

class FileDataSource @Inject constructor(
    private val client: HttpClient,
) {

    suspend fun fetchDownloadLink(
        words: List<String>,
    ): FileDownloadResponse {
        delay(5000L)
        return FileDownloadResponse(
            fileLink = "https://pdfobject.com/pdf/sample.pdf",
            fileName = "sample.pdf"
        )
    }

    suspend fun uploadFile(
        file: File,
        name: String,
    ): FileUploadResponse {
        delay(3000L)
        return FileUploadResponse(
            words = listOf(
                "apple",
                "car",
                "rain",
            )
        )
    }
}
