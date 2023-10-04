package com.actively.auth.ui

data class TextFieldState(
    val value: String,
    val isError: Boolean = false,
    val errorText: String? = null
)
