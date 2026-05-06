package com.openagent.manager.di

import com.openagent.core.network.openclaw.OpenClawRepository
import com.openagent.core.network.openclaw.OpenClawWebSocketManager
import com.openagent.core.network.hermes.HermesApiClient
import com.openagent.core.network.hermes.HermesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOpenClawWebSocketManager(): OpenClawWebSocketManager {
        return OpenClawWebSocketManager()
    }

    @Provides
    @Singleton
    fun provideOpenClawRepository(
        wsManager: OpenClawWebSocketManager
    ): OpenClawRepository {
        return OpenClawRepository(wsManager)
    }

    @Provides
    @Singleton
    fun provideHermesApiClient(): HermesApiClient {
        return HermesApiClient()
    }

    @Provides
    @Singleton
    fun provideHermesRepository(
        apiClient: HermesApiClient
    ): HermesRepository {
        return HermesRepository(apiClient)
    }
}
