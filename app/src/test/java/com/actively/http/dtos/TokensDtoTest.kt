package com.actively.http.dtos

import com.actively.asserts.shouldBe
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.plugins.auth.providers.BearerTokens

class TokensDtoTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest

    test("Maps TokensDto to BearerTokens") {
        val dto = TokensDto("access-token", "refresh-token")
        val tokens = BearerTokens("access-token", "refresh-token")
        dto.toBearerTokens() shouldBe tokens
    }

    test("Maps BearerTokens to TokensDto") {
        val tokens = BearerTokens("access-token", "refresh-token")
        val dto = TokensDto("access-token", "refresh-token")
        tokens.toDto() shouldBe dto
    }
})
