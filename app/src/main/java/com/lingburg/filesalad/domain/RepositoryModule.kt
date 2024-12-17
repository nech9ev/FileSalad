package com.lingburg.filesalad.domain

import com.lingburg.filesalad.data.DownloadRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoryModule {

    @Binds
    fun bindDownloadRepository(impl: DownloadRepositoryImpl): DownloadRepository
}
