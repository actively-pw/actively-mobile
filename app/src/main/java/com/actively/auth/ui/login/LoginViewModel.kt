package com.actively.auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.actively.auth.AuthResult
import com.actively.auth.Credentials
import com.actively.auth.usecases.LogInUseCase
import com.actively.field.Field
import com.actively.field.Validator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(private val logInUseCase: LogInUseCase) : ViewModel() {

    private val emailField = Field(Validator.Email)
    private val passwordField = Field(Validator.lengthInRange(8..50))

    val email = emailField.state
    val password = passwordField.state

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible = _isPasswordVisible.asStateFlow()

    private val _showLoginFailedDialog = MutableSharedFlow<Boolean>()
    val showLoginFailedDialog = _showLoginFailedDialog.asSharedFlow()

    fun onEmailChange(value: String) {
        emailField.value = value
    }

    fun onPasswordChange(value: String) {
        passwordField.value = value
    }

    fun changePasswordVisibility() {
        _isPasswordVisible.update { !it }
    }

    fun onSuccessfulLogin(block: () -> Unit) {
        emailField.validate()
        passwordField.validate()
        val areCredentialsValid = emailField.isValid && passwordField.isValid
        if (!areCredentialsValid) return
        viewModelScope.launch {
            val credentials = Credentials.Login(emailField.value, passwordField.value)
            when (logInUseCase(credentials)) {
                is AuthResult.Success -> block()
                is AuthResult.InvalidCredentials -> onShowLoginFailedDialog()
                else -> {}
            }
        }
    }

    fun onDismissLoginFailedDialog() = viewModelScope.launch {
        _showLoginFailedDialog.emit(false)
    }

    private fun onShowLoginFailedDialog() = viewModelScope.launch {
        _showLoginFailedDialog.emit(true)
    }
}
