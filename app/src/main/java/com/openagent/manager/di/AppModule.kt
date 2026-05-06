package com.openagent.manager.di

import android.content.Context
import androidx.room.Room
import com.openagent.core.network.local.AppDatabase
import com.openagent.core.network.local.ScriptDao
import com.openagent.core.network.local.SettingsDataStore
import com.openagent.core.network.hermes.HermesApiClient
import com.openagent.core.network.hermes.HermesRepository
import com.openagent.core.network.openclaw.OpenClawRepository
import com.openagent.core.network.openclaw.OpenClawWebSocketManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ── Context ─────────────────────────────

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    // ── DataStore ───────────────────────────

    @Provides
    @Singleton
    fun provideSettingsDataStore(context: Context): SettingsDataStore =
        SettingsDataStore(context)

    // ── Room Database ───────────────────────

    @Provides
    @Singleton
    fun provideAppDatabase(context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "openagent_db").build()

    @Provides
    fun provideScriptDao(db: AppDatabase): ScriptDao = db.scriptDao()

    // ── OpenClaw ────────────────────────────

    @Provides
    @Singleton
    fun provideOpenClawWebSocketManager(): OpenClawWebSocketManager =
        OpenClawWebSocketManager()

    @Provides
    @Singleton
    fun provideOpenClawRepository(wsManager: OpenClawWebSocketManager): OpenClawRepository =
        OpenClawRepository(wsManager)

    // ── Hermes ──────────────────────────────

    @Provides
    @Singleton
    fun provideHermesApiClient(): HermesApiClient = HermesApiClient()

    @Provides
    @Singleton
    fun provideHermesRepository(apiClient: HermesApiClient): HermesRepository =
        HermesRepository(apiClient)
}
