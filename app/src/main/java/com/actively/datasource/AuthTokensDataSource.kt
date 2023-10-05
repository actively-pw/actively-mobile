package com.actively.datasource

interface AuthTokensDataSource {

    fun getAccessToken(): String?

    fun setAccessToken(accessToken: String)

    fun getRefreshToken(): String?

    fun setRefreshToken(refreshToken: String)
}
