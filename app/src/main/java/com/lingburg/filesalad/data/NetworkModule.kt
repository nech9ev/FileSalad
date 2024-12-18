package com.lingburg.filesalad.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    internal fun provideHttpClient(
        okHttpClient: OkHttpClient,
        json: Json,
    ) = HttpClient(OkHttp) {
        engine {
            preconfigured = okHttpClient
            config {
                retryOnConnectionFailure(true)
            }
        }
        install(DefaultRequest) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }
        install(ContentNegotiation) {
            this.json(json)
        }
    }

    @Provides
    @Singleton
    internal fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .apply {
                addNetworkInterceptor(LoggingInterceptor())
            }
            .build()

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    fun provideJson() = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
        allowTrailingComma = true
    }
}
