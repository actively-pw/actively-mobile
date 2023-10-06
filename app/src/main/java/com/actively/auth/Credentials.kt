package com.actively.auth

sealed class Credentials {
    data class Login(val email: String, val password: String) : Credentials()
    data class Register(
        val name: String,
        val surname: String,
        val email: String,
        val password: String
    ) : Credentials()
}
