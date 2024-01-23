package com.actively.http.dtos

import com.actively.auth.Credentials
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CredentialsDtoTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest

    test("Maps login credentials to dto") {
        val credentials = Credentials.Login("user@mail.com", "password")
        val expectedDto = LoginCredentialsDto("user@mail.com", "password")
        credentials.toDto() shouldBe expectedDto
    }

    test("Maps register credentials to dto") {
        val credentials = Credentials.Register("user", "surname", "user@mail.com", "password")
        val expectedDto = RegisterCredentialsDto("user", "surname", "user@mail.com", "password")
        credentials.toDto() shouldBe expectedDto
    }
})
