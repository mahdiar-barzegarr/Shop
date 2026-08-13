package com.example.loginproject.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val PHONE_NUMBER = stringPreferencesKey("phone_number")
        val FULL_NAME = stringPreferencesKey("full_name")
    }

    val accessTokenFlow: Flow<String?> =
        context.dataStore.data.map { it[Keys.ACCESS_TOKEN] }

    val refreshTokenFlow: Flow<String?> =
        context.dataStore.data.map { it[Keys.REFRESH_TOKEN] }

    val fullNameFlow: Flow<String?> =
        context.dataStore.data.map { it[Keys.FULL_NAME] }

    fun getAccessTokenBlocking(): String? = runBlocking { accessTokenFlow.first() }
    fun getRefreshTokenBlocking(): String? = runBlocking { refreshTokenFlow.first() }

    suspend fun saveSession(accessToken: String, refreshToken: String, phoneNumber: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ACCESS_TOKEN] = accessToken
            prefs[Keys.REFRESH_TOKEN] = refreshToken
            prefs[Keys.PHONE_NUMBER] = phoneNumber
        }
    }

    fun updateAccessTokenBlocking(newAccessToken: String) = runBlocking {
        context.dataStore.edit { prefs -> prefs[Keys.ACCESS_TOKEN] = newAccessToken }
    }

    fun updateRefreshTokenBlocking(newRefreshToken: String) = runBlocking {
        context.dataStore.edit { prefs -> prefs[Keys.REFRESH_TOKEN] = newRefreshToken }
    }

    suspend fun saveFullName(fullName: String) {
        context.dataStore.edit { prefs -> prefs[Keys.FULL_NAME] = fullName }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}
