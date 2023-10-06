package com.actively.form

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TextField(private val validator: ((String) -> Boolean)? = null) {

    private val _state = MutableStateFlow(State("", isValid = true))
    val state = _state.asStateFlow()

    fun setValue(value: String) = _state.update {
        val isValid = if (it.isValid && validator != null) {
            validator.invoke(value)
        } else {
            true
        }
        it.copy(value = value, isValid = isValid)
    }

    fun validate() {
        if (validator == null) return
        _state.update { it.copy(isValid = validator.invoke(it.value)) }
    }

    data class State(val value: String, val isValid: Boolean)
}

