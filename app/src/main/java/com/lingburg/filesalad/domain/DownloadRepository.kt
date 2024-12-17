package com.lingburg.filesalad.domain

import com.lingburg.filesalad.data.datasource.DownloadResponse

interface DownloadRepository {

    suspend fun getDownloadLink(
        words: List<String>,
    ): DownloadResponse
}
