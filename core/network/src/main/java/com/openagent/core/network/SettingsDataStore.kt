package com.openagent.core.network

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "openagent_settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val OPENCLAW_GATEWAY_URL = stringPreferencesKey("openclaw_gateway_url")
        val OPENCLAW_TOKEN = stringPreferencesKey("openclaw_token")
        val HERMES_SERVER_URL = stringPreferencesKey("hermes_server_url")
        val HERMES_API_KEY = stringPreferencesKey("hermes_api_key")
        val HERMES_API_MODE = stringPreferencesKey("hermes_api_mode")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val ENCRYPTION_ENABLED = booleanPreferencesKey("encryption_enabled")
        val LANGUAGE = stringPreferencesKey("language")
    }

    val gatewayUrl: Flow<String> = context.dataStore.data.map { it[OPENCLAW_GATEWAY_URL] ?: "" }
    val gatewayToken: Flow<String> = context.dataStore.data.map { it[OPENCLAW_TOKEN] ?: "" }
    val hermesUrl: Flow<String> = context.dataStore.data.map { it[HERMES_SERVER_URL] ?: "" }
    val hermesApiKey: Flow<String> = context.dataStore.data.map { it[HERMES_API_KEY] ?: "" }
    val hermesApiMode: Flow<String> = context.dataStore.data.map { it[HERMES_API_MODE] ?: "CHAT_COMPLETIONS" }
    val darkMode: Flow<Boolean> = context.dataStore.data.map { it[DARK_MODE] ?: false }
    val biometricEnabled: Flow<Boolean> = context.dataStore.data.map { it[BIOMETRIC_ENABLED] ?: false }
    val encryptionEnabled: Flow<Boolean> = context.dataStore.data.map { it[ENCRYPTION_ENABLED] ?: false }
    val language: Flow<String> = context.dataStore.data.map { it[LANGUAGE] ?: "zh-CN" }

    suspend fun saveGatewayUrl(url: String) {
        context.dataStore.edit { it[OPENCLAW_GATEWAY_URL] = url }
    }

    suspend fun saveGatewayToken(token: String) {
        context.dataStore.edit { it[OPENCLAW_TOKEN] = token }
    }

    suspend fun saveHermesUrl(url: String) {
        context.dataStore.edit { it[HERMES_SERVER_URL] = url }
    }

    suspend fun saveHermesApiKey(key: String) {
        context.dataStore.edit { it[HERMES_API_KEY] = key }
    }

    suspend fun saveHermesApiMode(mode: String) {
        context.dataStore.edit { it[HERMES_API_MODE] = mode }
    }

    suspend fun saveDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[DARK_MODE] = enabled }
    }

    suspend fun saveBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { it[BIOMETRIC_ENABLED] = enabled }
    }

    suspend fun saveEncryptionEnabled(enabled: Boolean) {
        context.dataStore.edit { it[ENCRYPTION_ENABLED] = enabled }
    }

    suspend fun saveLanguage(lang: String) {
        context.dataStore.edit { it[LANGUAGE] = lang }
    }
}
