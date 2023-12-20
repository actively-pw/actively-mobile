package com.actively.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.actively.R
import com.actively.activity.RecordedActivity
import com.actively.details.usecase.DeleteActivityUseCase
import com.actively.distance.Distance.Companion.inKilometers
import com.actively.distance.Distance.Companion.inWholeMeters
import com.actively.home.usecase.GetDetailedRecordedActivityUseCase
import com.actively.recorder.ui.format
import com.actively.util.TimeProvider
import com.actively.util.getActivityTimeString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ActivityDetailsViewModel(
    private val id: RecordedActivity.Id,
    getDetailedRecordedActivityUseCase: GetDetailedRecordedActivityUseCase,
    timeProvider: TimeProvider,
    private val deleteActivityUseCase: DeleteActivityUseCase
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
                            R.string.distance_km to stats.distance.inKilometers.trim(),
                            R.string.max_speed_kmph to stats.maxSpeed.trim()
                        ),
                        DetailsRow(
                            R.string.time to stats.totalTime.format(),
                            R.string.sum_ascent_meters to stats.sumOfAscent.inWholeMeters.toString()
                        ),
                        DetailsRow(
                            R.string.avg_speed_km_h to stats.averageSpeed.trim(),
                            R.string.sum_descent_meters to stats.sumOfDescent.inWholeMeters.toString()
                        ),
                    )
                )
            } ?: DetailsScreenState.Error
        }
    }

    fun onDeleteClick() = _state.update {
        (it as DetailsScreenState.Loaded).copy(showConfirmDeleteDialog = true)
    }

    fun onConfirmDelete() = viewModelScope.launch {
        deleteActivityUseCase(id)
        onDiscardDialogue()
    }

    fun onDiscardDialogue() = _state.update {
        (it as DetailsScreenState.Loaded).copy(showConfirmDeleteDialog = false)
    }

    private fun Double.trim() = String.format("%.2f", this)
}
