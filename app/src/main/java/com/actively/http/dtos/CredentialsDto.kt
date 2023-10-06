package com.actively.http.dtos

import com.actively.auth.Credentials
import kotlinx.serialization.Serializable

@Serializable
data class CredentialsDto(
    val email: String,
    val password: String
)

fun Credentials.toDto() = CredentialsDto(
    email = email,
    password = password
)
