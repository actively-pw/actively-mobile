package com.actively.details.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.actively.activity.RecordedActivity
import com.actively.details.usecase.GetDynamicMapDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DynamicMapViewModel(
    id: RecordedActivity.Id,
    getDynamicMapDataUseCase: GetDynamicMapDataUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DynamicMapState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val result = getDynamicMapDataUseCase(id)
            result.getOrNull()?.let { data ->
                _state.update {
                    it.copy(routeUrl = data.geoJsonUrl, isLoading = false)
                }
            } ?: _state.update { it.copy(isLoading = false, isError = true) }
        }
    }
}
