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

    fun onNameChange(value: String) = nameField.setValue(value)

    fun onSurnameChange(value: String) = surnameField.setValue(value)

    fun onEmailChange(value: String) = emailField.setValue(value)

    fun onPasswordChange(value: String) = passwordField.setValue(value)

    fun changePasswordVisibility() {
        _isPasswordVisible.update { !it }
    }

    fun onSuccessfulRegister(block: () -> Unit) {
        validateFields()
        val name = name.value
        val surname = surname.value
        val email = email.value
        val password = password.value
        val areCredentialsValid =
            name.isValid && surname.isValid && email.isValid && password.isValid
        if (!areCredentialsValid) return
        viewModelScope.launch {
            val credentials = Credentials.Register(
                name = name.value,
                surname = surname.value,
                email = email.value,
                password = password.value
            )
            when (registerUseCase(credentials)) {
                is AuthResult.Success -> block()
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

    private fun validateFields() {
        nameField.validate()
        surnameField.validate()
        emailField.validate()
        passwordField.validate()
    }
}
