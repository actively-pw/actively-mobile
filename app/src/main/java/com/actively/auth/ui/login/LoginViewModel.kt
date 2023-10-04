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

    private val emailRegex = Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")

    fun onEmailChange(value: String) {
        _email.update {
            it.copy(
                value = value,
                isError = if (it.isError) !isEmailValid(value) else it.isError
            )
        }
    }

    fun onPasswordChange(value: String) {
        _password.update {
            it.copy(
                value = value,
                isError = if (it.isError) !isPasswordValid(value) else it.isError
            )
        }
    }

    fun validateFields(onSuccess: () -> Unit) {
        _email.update { it.copy(isError = !isEmailValid(it.value)) }
        _password.update { it.copy(isError = !isPasswordValid(it.value)) }
        if (!_email.value.isError && !_password.value.isError) {
            onSuccess()
        }
    }

    private fun isEmailValid(email: String) = email.isNotEmpty() && emailRegex.matches(email)

    private fun isPasswordValid(password: String) = password.length in 8..50
}
