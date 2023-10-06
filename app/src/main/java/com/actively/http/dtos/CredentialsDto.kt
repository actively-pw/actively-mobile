package com.actively.http.dtos

import com.actively.auth.Credentials
import kotlinx.serialization.Serializable

@Serializable
data class LoginCredentialsDto(
    val email: String,
    val password: String
)

fun Credentials.Login.toDto() = LoginCredentialsDto(
    email = email,
    password = password
)

@Serializable
data class RegisterCredentialsDto(
    val name: String,
    val surname: String,
    val email: String,
    val password: String
)

fun Credentials.Register.toDto() = RegisterCredentialsDto(
    name = "user",
    surname = "surname",
    email = email,
    password = password
)
