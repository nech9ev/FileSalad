package com.lingburg.filesalad.domain

import com.lingburg.filesalad.data.datasource.DownloadResponse
import javax.inject.Inject

class DownloadInteractor @Inject constructor(
    private val repository: DownloadRepository,
) {

    suspend fun getDownloadLink(
        words: List<String>,
    ): DownloadResponse {
        return repository.getDownloadLink(words)
    }
}
