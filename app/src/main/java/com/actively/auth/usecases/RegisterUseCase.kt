package com.actively.auth.usecases

import com.actively.auth.AuthResult
import com.actively.auth.Credentials

interface RegisterUseCase {

    suspend operator fun invoke(credentials: Credentials): AuthResult
}
