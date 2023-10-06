package com.actively.auth

sealed class AuthResult {
    object Success : AuthResult()
    object AccountExists : AuthResult()
    object InvalidCredentials : AuthResult()
    object Error : AuthResult()
}
