package com.actively.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.actively.activity.RecordedActivity
import com.actively.distance.Distance.Companion.inKilometers
import com.actively.distance.Distance.Companion.inWholeMeters
import com.actively.home.usecase.GetDetailedRecordedActivityUseCase
import com.actively.recorder.ui.format
import com.actively.util.TimeProvider
import com.actively.util.getActivityTimeString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ActivityDetailsViewModel(
    id: RecordedActivity.Id,
    getDetailedRecordedActivityUseCase: GetDetailedRecordedActivityUseCase,
    timeProvider: TimeProvider,
) : ViewModel() {

    private val _state = MutableStateFlow<DetailsScreenState>(DetailsScreenState.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = DetailsScreenState.Loading
            _state.value = getDetailedRecordedActivityUseCase(id).getOrNull()?.let {
                val stats = it.stats
                DetailsScreenState.Loaded(
                    title = it.title,
                    imageUrl = it.mapUrl,
                    typeOfActivity = it.sport,
                    time = getActivityTimeString(start = it.start, now = timeProvider()),
                    showConfirmDeleteDialog = false,
                    details = listOf(
                        DetailsRow(
                            "Distance (km)" to stats.distance.inKilometers.trim(),
                            "Max speed (km/h)" to stats.maxSpeed.trim()
                        ),
                        DetailsRow(
                            "Total time" to stats.totalTime.format(),
                            "Sum of Ascent (m)" to stats.sumOfAscent.inWholeMeters.toString()
                        ),
                        DetailsRow(
                            "Avg speed (km/h)" to stats.averageSpeed.trim(),
                            "Sum of descent (m)" to stats.sumOfDescent.inWholeMeters.toString()
                        ),
                    )
                )
            } ?: DetailsScreenState.Error
        }
    }

    private fun Double.trim() = String.format("%.2f", this)
}
