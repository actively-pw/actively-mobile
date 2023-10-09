package com.actively.asserts

import io.kotest.assertions.assertionCounter
import io.kotest.assertions.collectOrThrow
import io.kotest.assertions.eq.eq
import io.kotest.assertions.errorCollector
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.mockk.MockKMatcherScope

infix fun BearerTokens?.shouldBe(expected: BearerTokens?): BearerTokens? {
    val actual = this
    assertionCounter.inc()
    eq(actual?.accessToken, expected?.accessToken)?.let(errorCollector::collectOrThrow)
    eq(actual?.refreshToken, expected?.refreshToken)?.let(errorCollector::collectOrThrow)
    return this
}

fun MockKMatcherScope.tokensEq(value: BearerTokens): BearerTokens = match {
    it.accessToken == value.accessToken && it.refreshToken == value.refreshToken
}
