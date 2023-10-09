package com.actively.http.dtos

import io.ktor.client.plugins.auth.providers.BearerTokens
import kotlinx.serialization.Serializable

@Serializable
data class TokensDto(val jwt: String, val refreshToken: String)

fun TokensDto.toBearerTokens() = BearerTokens(
    accessToken = jwt,
    refreshToken = refreshToken
)

fun BearerTokens.toDto() = TokensDto(
    jwt = accessToken,
    refreshToken = refreshToken
)
