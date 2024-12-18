package com.lingburg.filesalad.data.datasource

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class FileDownloadResponse(
    @SerialName("fileName")
    val fileName: String,
)
