package com.actively.details.map

import androidx.lifecycle.ViewModel
import com.actively.activity.RecordedActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DynamicMapViewModel(
    id: RecordedActivity.Id
) : ViewModel() {

    private val _state = MutableStateFlow(DynamicMapState())
    val state = _state.asStateFlow()
}
