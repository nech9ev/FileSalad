package com.lingburg.filesalad.data.datasource

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class FileDownloadResponse(
    @SerialName("link")
    val fileLink: String,
    @SerialName("name")
    val fileName: String,
)
