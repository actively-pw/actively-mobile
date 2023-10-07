package com.actively.field

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue

class ValidatorTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest

    test("Validator.None always returns true") {
        val validator = Validator.None
        validator("").shouldBeTrue()
        validator("ab").shouldBeTrue()
        validator("string xd").shouldBeTrue()
    }

    test("Validator.NonEmptyString returns true if string is not empty") {
        val validator = Validator.NonEmptyString
        validator("").shouldBeFalse()
        validator("string").shouldBeTrue()
    }

    test("Validator.Email returns true if string is a valid email") {
        val validator = Validator.Email
        validator("").shouldBeFalse()
        validator("mail").shouldBeFalse()
        validator("mail.").shouldBeFalse()
        validator("mail.a@").shouldBeFalse()
        validator("mail@").shouldBeFalse()
        validator("mail.com").shouldBeFalse()
        validator("mail@mail").shouldBeFalse()
        validator("mail@mail.").shouldBeFalse()
        validator("mail@mail.c").shouldBeFalse()
        validator("@mail.com").shouldBeFalse()
        validator("mail@.com").shouldBeFalse()
        validator("mail@mail.comadsd").shouldBeFalse()
        validator("mail@mail.com").shouldBeTrue()
    }

    test("Validator.lengthInRange returns true if string's length is in range") {
        val validator = Validator.lengthInRange(5..10)
        validator("").shouldBeFalse()
        validator("asdf").shouldBeFalse()
        validator("asdfg").shouldBeTrue()
        validator("a".repeat(10)).shouldBeTrue()
        validator("a".repeat(11)).shouldBeFalse()
    }
})
