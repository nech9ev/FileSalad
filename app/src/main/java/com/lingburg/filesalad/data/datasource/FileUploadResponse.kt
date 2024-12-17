package com.lingburg.filesalad.data.datasource

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FileUploadResponse(
    @SerialName("words")
    val words: List<String>,
)
