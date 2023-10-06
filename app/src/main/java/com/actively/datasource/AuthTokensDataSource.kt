package com.actively.datasource

import android.content.SharedPreferences
import androidx.core.content.edit

interface AuthTokensDataSource {

    fun getAccessToken(): String?

    fun setAccessToken(accessToken: String)

    fun getRefreshToken(): String?

    fun setRefreshToken(refreshToken: String)

    fun clearTokens()
}

class AuthTokensDataSourceImpl(
    private val sharedPreferences: SharedPreferences
) : AuthTokensDataSource {

    override fun getAccessToken() = sharedPreferences.getString(ACCESS_TOKEN_KEY, null)

    override fun setAccessToken(accessToken: String) = sharedPreferences.edit {
        putString(ACCESS_TOKEN_KEY, accessToken)
    }

    override fun getRefreshToken() = sharedPreferences.getString(REFRESH_TOKEN_KEY, null)

    override fun setRefreshToken(refreshToken: String) = sharedPreferences.edit {
        putString(REFRESH_TOKEN_KEY, refreshToken)
    }

    override fun clearTokens() = sharedPreferences.edit {
        putString(ACCESS_TOKEN_KEY, null)
        putString(REFRESH_TOKEN_KEY, null)
    }

    private companion object {
        const val ACCESS_TOKEN_KEY = "access_token"
        const val REFRESH_TOKEN_KEY = "refresh_token"
    }
}
