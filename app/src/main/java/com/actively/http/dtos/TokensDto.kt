package com.actively.http.dtos

import com.actively.auth.Tokens
import kotlinx.serialization.Serializable

@Serializable
data class TokensDto(val jwt: String, val refreshToken: String)

fun TokensDto.toTokens() = Tokens(
    accessToken = jwt,
    refreshToken = refreshToken
)

fun Tokens.toDto() = TokensDto(
    jwt = accessToken,
    refreshToken = refreshToken
)
