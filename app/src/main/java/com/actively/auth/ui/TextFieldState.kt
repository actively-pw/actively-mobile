package com.actively.auth.ui

data class TextFieldState(
    val value: String,
    val isValid: Boolean = true,
    val errorText: String? = null
)
