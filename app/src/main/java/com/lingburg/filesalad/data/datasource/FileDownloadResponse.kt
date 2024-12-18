package com.lingburg.filesalad.data.datasource

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class FileDownloadResponse(
    @SerialName("downloadLink")
    val fileLink: String,
    @SerialName("fileName")
    val fileName: String,
)
