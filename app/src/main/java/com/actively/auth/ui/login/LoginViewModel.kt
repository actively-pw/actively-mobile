package com.actively.auth.ui.login

import androidx.lifecycle.ViewModel
import com.actively.auth.ui.TextFieldState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val _email = MutableStateFlow(TextFieldState(value = ""))
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow(TextFieldState(value = ""))
    val password = _password.asStateFlow()

    fun onEmailChange(value: String) {
        _email.update { it.copy(value = value) }
    }

    fun onPasswordChange(value: String) {
        _password.update { it.copy(value = value) }
    }
}
