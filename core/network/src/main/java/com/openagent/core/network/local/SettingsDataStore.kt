package com.openagent.core.network.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "openagent_settings")

/**
 * 本地配置持久化存储
 * 保存 Gateway/Hermes 连接信息、主题偏好、安全设置等
 */
@Singleton
class SettingsDataStore @Inject constructor(
    private val context: Context
) {
    companion object {
        val OC_GATEWAY_URL = stringPreferencesKey("oc_gateway_url")
        val OC_TOKEN = stringPreferencesKey("oc_token")
        val HERMES_URL = stringPreferencesKey("hermes_url")
        val HERMES_KEY = stringPreferencesKey("hermes_key")
        val HERMES_MODE = stringPreferencesKey("hermes_mode")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val BIOMETRIC = booleanPreferencesKey("biometric")
        val ENCRYPTION = booleanPreferencesKey("encryption")
        val LANGUAGE = stringPreferencesKey("language")
    }

    val gatewayUrl: Flow<String> = context.dataStore.data.map { it[OC_GATEWAY_URL] ?: "" }
    val gatewayToken: Flow<String> = context.dataStore.data.map { it[OC_TOKEN] ?: "" }
    val hermesUrl: Flow<String> = context.dataStore.data.map { it[HERMES_URL] ?: "" }
    val hermesApiKey: Flow<String> = context.dataStore.data.map { it[HERMES_KEY] ?: "" }
    val hermesApiMode: Flow<String> = context.dataStore.data.map { it[HERMES_MODE] ?: "CHAT_COMPLETIONS" }
    val darkMode: Flow<Boolean> = context.dataStore.data.map { it[DARK_MODE] ?: false }
    val biometricEnabled: Flow<Boolean> = context.dataStore.data.map { it[BIOMETRIC] ?: false }
    val encryptionEnabled: Flow<Boolean> = context.dataStore.data.map { it[ENCRYPTION] ?: false }
    val language: Flow<String> = context.dataStore.data.map { it[LANGUAGE] ?: "zh-CN" }

    suspend fun saveGatewayUrl(v: String) { context.dataStore.edit { it[OC_GATEWAY_URL] = v } }
    suspend fun saveGatewayToken(v: String) { context.dataStore.edit { it[OC_TOKEN] = v } }
    suspend fun saveHermesUrl(v: String) { context.dataStore.edit { it[HERMES_URL] = v } }
    suspend fun saveHermesApiKey(v: String) { context.dataStore.edit { it[HERMES_KEY] = v } }
    suspend fun saveHermesApiMode(v: String) { context.dataStore.edit { it[HERMES_MODE] = v } }
    suspend fun saveDarkMode(v: Boolean) { context.dataStore.edit { it[DARK_MODE] = v } }
    suspend fun saveBiometric(v: Boolean) { context.dataStore.edit { it[BIOMETRIC] = v } }
    suspend fun saveEncryption(v: Boolean) { context.dataStore.edit { it[ENCRYPTION] = v } }
    suspend fun saveLanguage(v: String) { context.dataStore.edit { it[LANGUAGE] = v } }
}
