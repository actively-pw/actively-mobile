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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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

    private val _controlsState = MutableStateFlow(
        ControlsState(current = RecorderState.Idle, previous = RecorderState.Idle)
    )
    val controlsState: StateFlow<ControlsState> = _controlsState.asStateFlow()

    init {
        getRecorderStateUseCase().onEach { newState ->
            _controlsState.update { currentState ->
                ControlsState(current = newState, previous = currentState.current)
            }
            println(_controlsState.value)
        }.launchIn(viewModelScope)
    }

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





