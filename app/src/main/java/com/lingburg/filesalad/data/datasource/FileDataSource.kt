package com.lingburg.filesalad.data.datasource


import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class FileDataSource @Inject constructor(
    private val client: HttpClient,
) {

    suspend fun fetchDownloadLink(
        words: List<String>,
    ): FileDownloadResponse = client.get("http://45.89.26.244:8080/files/download") {
        parameter("firstWord", words[0])
        parameter("secondWord", words[1])
        parameter("thirdWord", words[2])
        contentType(ContentType.Application.Json)
    }.body()

    suspend fun uploadFile(
        file: File,
        name: String,
    ): FileUploadResponse =
        client.submitFormWithBinaryData(
            url = "http://45.89.26.244:8080/files/upload",
            formData = formData {
                append("\"file\"", file.readBytes(), Headers.build {
                    append(HttpHeaders.ContentType, ContentType.Any.toString())
                    append(HttpHeaders.ContentDisposition, "filename=\"$name\"")
                })
            },
        ).body()
}
