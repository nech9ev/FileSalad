package com.lingburg.filesalad.data.datasource

import io.ktor.client.HttpClient
import javax.inject.Inject

class DownloadDataSource @Inject constructor(
    private val client: HttpClient,
) {

    suspend fun fetchDownloadLink(
        words: List<String>,
    ): DownloadResponse = DownloadResponse(
        fileLink = "https://pdfobject.com/pdf/sample.pdf",
        fileName = "sample.pdf"
    )
}
