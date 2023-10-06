package com.actively.field

import app.cash.turbine.test
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class FieldTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    val validator = mockk<Validator>()

    test("Initial state is empty string along with isValid set to true") {
        val field = Field()
        val state = field.state.value
        state.value shouldBe ""
        state.isValid.shouldBeTrue()
    }

    test("set value should set state") {
        val field = Field()
        field.state.test {
            awaitItem().value shouldBe ""
            field.value = "string"
            awaitItem().value shouldBe "string"
            field.value = "xd"
            awaitItem().value shouldBe "xd"
        }
    }

    test("get value should return field value from state") {
        val field = Field()
        field.value = "string"
        field.value shouldBe "string"
        field.state.value.value shouldBe "string"
    }

    test("set value does not perform validation if value is valid") {
        val field = Field(validator)
        every { validator(any()) } returns false
        field.value = "string"
        verify(exactly = 0) { validator(any()) }
    }

    test("set value performs validation if value is not valid") {
        val field = Field(validator)
        every { validator("") } returns false
        every { validator("invalid string") } returns false
        every { validator("valid string") } returns true
        field.validate()
        field.value = "invalid string"
        field.value = "valid string"
        field.value = "next valid string"
        verify(exactly = 1) { validator("") }
        verify(exactly = 1) { validator("invalid string") }
        verify(exactly = 1) { validator("valid string") }
        verify(exactly = 0) { validator("next valid string") }
    }


    test("set value sets correct states if field was invalid") {
        val field = Field(validator)
        every { validator("") } returns false
        every { validator("invalid string") } returns false
        every { validator("valid string") } returns true
        field.state.test {
            awaitItem()
            field.validate()
            awaitItem() shouldBe Field.State("", false)
            field.value = "invalid string"
            awaitItem() shouldBe Field.State("invalid string", false)
            field.value = "valid string"
            awaitItem() shouldBe Field.State("valid string", true)
            field.value = "next valid string"
            awaitItem() shouldBe Field.State("next valid string", true)
        }
    }

    test("validate calls validator") {
        val field = Field(validator)
        every { validator(any()) } returns false
        field.validate()
        verify(exactly = 1) { validator("") }
    }

    test("validate sets validator result to state") {
        val field = Field(validator)
        every { validator("invalid string") } returns false
        field.state.test {
            awaitItem()
            field.value = "invalid string"
            awaitItem()
            field.validate()
            awaitItem() shouldBe Field.State("invalid string", false)
        }
    }
})
