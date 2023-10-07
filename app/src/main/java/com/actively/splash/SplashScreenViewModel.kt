package com.actively.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.actively.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SplashScreenViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    init {
        viewModelScope.launch {
            _isLoggedIn.update { authRepository.isUserLoggedIn() }
        }
    }
}
