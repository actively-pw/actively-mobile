package com.actively.field

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class Field(private val validator: Validator = Validator.None) {

    private val _state = MutableStateFlow(State("", isValid = true))
    val state = _state.asStateFlow()

    fun setValue(value: String) = _state.update {
        it.copy(
            value = value,
            isValid = if (!it.isValid) validator(value) else true
        )
    }

    fun validate() {
        _state.update { it.copy(isValid = validator(it.value)) }
    }

    data class State(val value: String, val isValid: Boolean)
}

