package com.actively.recorder.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.actively.activity.Location
import com.actively.activity.RouteSlice
import com.actively.recorder.RecorderState
import com.actively.recorder.usecase.GetRecorderStateUseCase
import com.actively.recorder.usecase.RecordingControlUseCases
import com.actively.repository.ActivityRecordingRepository
import com.actively.util.TimeProvider
import com.mapbox.geojson.LineString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecorderViewModel(
    private val recordingControlUseCases: RecordingControlUseCases,
    private val timeProvider: TimeProvider,
    getRecorderStateUseCase: GetRecorderStateUseCase,
    activityRecordingRepository: ActivityRecordingRepository,
) : ViewModel() {

    val stats = activityRecordingRepository.getStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    val route = activityRecordingRepository.getRoute()
        .toGeoJsonFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    val recordingState = getRecorderStateUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), RecorderState.Idle)

    fun startRecording() = viewModelScope.launch {
        recordingControlUseCases.startRecording("Cycling", timeProvider())
    }

    fun pauseRecording() {
        recordingControlUseCases.pauseRecording()
    }

    fun resumeRecording() = viewModelScope.launch {
        recordingControlUseCases.resumeRecording(timeProvider())
    }

    fun stopRecording() = viewModelScope.launch {
        recordingControlUseCases.stopRecording()
    }

    private fun Flow<List<RouteSlice>>.toGeoJsonFlow() = mapNotNull { slices ->
        slices.flatMap(RouteSlice::locations)
            .takeIf { it.size >= 2 }
            ?.toGeoJson()
    }

    private fun List<Location>.toGeoJson() = LineString.fromLngLats(map(Location::toPoint)).toJson()
}





