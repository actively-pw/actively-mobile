package com.actively.auth.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.actively.auth.AuthResult
import com.actively.auth.Credentials
import com.actively.auth.usecases.RegisterUseCase
import com.actively.field.Field
import com.actively.field.Validator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(private val registerUseCase: RegisterUseCase) : ViewModel() {

    private val nameField = Field(Validator.NonEmptyString)
    private val surnameField = Field(Validator.NonEmptyString)
    private val emailField = Field(Validator.Email)
    private val passwordField = Field(Validator.lengthInRange(8..50))

    val name = nameField.state
    val surname = surnameField.state
    val email = emailField.state
    val password = passwordField.state

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible = _isPasswordVisible.asStateFlow()

    private val _showRegistrationFailedDialog = MutableSharedFlow<Boolean>()
    val showRegistrationFailedDialog = _showRegistrationFailedDialog.asSharedFlow()

    private val _registerInProgress = MutableStateFlow(false)
    val registerInProgress = _registerInProgress.asStateFlow()

    fun onNameChange(value: String) {
        nameField.value = value
    }

    fun onSurnameChange(value: String) {
        surnameField.value = value
    }

    fun onEmailChange(value: String) {
        emailField.value = value
    }

    fun onPasswordChange(value: String) {
        passwordField.value = value
    }

    fun changePasswordVisibility() {
        _isPasswordVisible.update { !it }
    }

    fun onSuccessfulRegister(block: () -> Unit) {
        if (_registerInProgress.value) return
        validateFields()
        val areCredentialsValid =
            nameField.isValid && surnameField.isValid && emailField.isValid && passwordField.isValid
        if (!areCredentialsValid) return
        _registerInProgress.update { true }
        viewModelScope.launch {
            val credentials = Credentials.Register(
                name = nameField.value,
                surname = surnameField.value,
                email = emailField.value,
                password = passwordField.value
            )
            when (registerUseCase(credentials)) {
                is AuthResult.Success -> block()
                is AuthResult.AccountExists -> onShowRegistrationFailedDialog()
                else -> {}
            }
            _registerInProgress.update { false }
        }
    }

    fun onDismissRegistrationFailedDialog() = viewModelScope.launch {
        _showRegistrationFailedDialog.emit(false)
    }

    private fun onShowRegistrationFailedDialog() = viewModelScope.launch {
        _showRegistrationFailedDialog.emit(true)
    }

    private fun validateFields() {
        nameField.validate()
        surnameField.validate()
        emailField.validate()
        passwordField.validate()
    }
}
