package com.lingburg.filesalad.data

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor

class LoggingInterceptor : Interceptor {

    private val default = HttpLoggingInterceptor().apply { level = DEFAULT_LEVEL }

    override fun intercept(chain: Interceptor.Chain): Response {
        return default.intercept(chain).also {
            default.level = DEFAULT_LEVEL
        }
    }

    private companion object {

        val DEFAULT_LEVEL = HttpLoggingInterceptor.Level.BODY
    }
}
