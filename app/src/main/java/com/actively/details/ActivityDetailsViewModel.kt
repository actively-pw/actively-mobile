package com.actively.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ActivityDetailsViewModel : ViewModel() {

    private val _state = MutableStateFlow<DetailsScreenState>(DetailsScreenState.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            delay(300)
            _state.value = DetailsScreenState.Loaded(
                title = "Lunch ride",
                imageUrl = "",
                typeOfActivity = "Ride",
                showConfirmDeleteDialog = false,
                details = listOf(
                    DetailsRow("Distance (km)" to "10.03", "Max speed (km/h)" to "30.41"),
                    DetailsRow("Total time" to "01:20:59", "Sum of Ascent (m)" to "420"),
                    DetailsRow("Avg speed (km/h)" to "20.59", "Sum of descent (m)" to "210"),
                )
            )
        }
    }
}
