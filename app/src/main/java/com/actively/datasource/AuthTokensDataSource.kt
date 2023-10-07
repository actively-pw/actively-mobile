package com.actively.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

interface AuthTokensDataSource {

    suspend fun getAccessToken(): String?

    suspend fun setAccessToken(accessToken: String)

    suspend fun getRefreshToken(): String?

    suspend fun setRefreshToken(refreshToken: String)

    suspend fun clearTokens()
}

class AuthTokensDataSourceImpl(
    private val dataStore: DataStore<Preferences>
) : AuthTokensDataSource {

    override suspend fun getAccessToken() = dataStore.data.map { it[ACCESS_TOKEN_KEY] }.first()

    override suspend fun setAccessToken(accessToken: String) {
        dataStore.edit { it[ACCESS_TOKEN_KEY] = accessToken }
    }

    override suspend fun getRefreshToken() = dataStore.data.map { it[REFRESH_TOKEN_KEY] }.first()

    override suspend fun setRefreshToken(refreshToken: String) {
        dataStore.edit { it[REFRESH_TOKEN_KEY] = refreshToken }
    }

    override suspend fun clearTokens() {
        dataStore.edit {
            it.remove(ACCESS_TOKEN_KEY)
            it.remove(REFRESH_TOKEN_KEY)
        }
    }

    private companion object {
        val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    }
}
