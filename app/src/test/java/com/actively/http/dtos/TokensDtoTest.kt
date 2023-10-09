package com.actively.http.dtos

import com.actively.auth.Tokens
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TokensDtoTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest

    test("Maps TokensDto to Tokens") {
        val dto = TokensDto("access-token", "refresh-token")
        val tokens = Tokens("access-token", "refresh-token")
        dto.toBearerTokens() shouldBe tokens
    }

    test("Maps Tokens to TokensDto") {
        val tokens = Tokens("access-token", "refresh-token")
        val dto = TokensDto("access-token", "refresh-token")
        tokens.toDto() shouldBe dto
    }
})
