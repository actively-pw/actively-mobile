package com.actively.details.map

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DynamicMapViewModel() : ViewModel() {

    private val _state = MutableStateFlow(DynamicMapState())
    val state = _state.asStateFlow()
}
