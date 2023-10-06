package com.actively.auth.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.actively.auth.AuthResult
import com.actively.auth.Credentials
import com.actively.auth.ui.TextFieldState
import com.actively.auth.usecases.RegisterUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(private val registerUseCase: RegisterUseCase) : ViewModel() {

    private val _email = MutableStateFlow(TextFieldState(value = ""))
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow(TextFieldState(value = ""))
    val password = _password.asStateFlow()

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible = _isPasswordVisible.asStateFlow()

    private val _showRegistrationFailedDialog = MutableSharedFlow<Boolean>()
    val showRegistrationFailedDialog = _showRegistrationFailedDialog.asSharedFlow()

    private val emailRegex = Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")

    fun onEmailChange(value: String) {
        _email.update {
            it.copy(
                value = value,
                isValid = if (!it.isValid) isEmailValid(value) else true
            )
        }
    }

    fun onPasswordChange(value: String) {
        _password.update {
            it.copy(
                value = value,
                isValid = if (!it.isValid) isPasswordValid(value) else true
            )
        }
    }

    fun changePasswordVisibility() {
        _isPasswordVisible.update { !it }
    }

    fun validateFields(onSuccess: () -> Unit) {
        _email.update { it.copy(isValid = isEmailValid(it.value)) }
        _password.update { it.copy(isValid = isPasswordValid(it.value)) }
        val emailField = _email.value
        val passwordField = _password.value
        val areCredentialsValid = emailField.isValid && passwordField.isValid
        if (!areCredentialsValid) return
        viewModelScope.launch {
            val credentials = Credentials(emailField.value, passwordField.value)
            when (registerUseCase(credentials)) {
                is AuthResult.Success -> onSuccess()
                is AuthResult.AccountExists -> onShowRegistrationFailedDialog()
                else -> {}
            }
        }
    }

    fun onDismissRegistrationFailedDialog() = viewModelScope.launch {
        _showRegistrationFailedDialog.emit(false)
    }

    private fun onShowRegistrationFailedDialog() = viewModelScope.launch {
        _showRegistrationFailedDialog.emit(true)
    }

    private fun isEmailValid(email: String) = email.isNotEmpty() && emailRegex.matches(email)

    private fun isPasswordValid(password: String) = password.length in 8..50
}
