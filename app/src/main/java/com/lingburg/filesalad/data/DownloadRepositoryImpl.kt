package com.lingburg.filesalad.data

import com.lingburg.filesalad.data.datasource.DownloadDataSource
import com.lingburg.filesalad.data.datasource.DownloadResponse
import com.lingburg.filesalad.domain.DownloadRepository
import javax.inject.Inject

class DownloadRepositoryImpl @Inject constructor(
    private val dataSource: DownloadDataSource,
) : DownloadRepository {

    override suspend fun getDownloadLink(
        words: List<String>,
    ): DownloadResponse {
        return dataSource.fetchDownloadLink(words)
    }
}
