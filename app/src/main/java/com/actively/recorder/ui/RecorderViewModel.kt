package com.actively.recorder.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.actively.activity.Location
import com.actively.activity.RouteSlice
import com.actively.recorder.RecorderState
import com.actively.recorder.usecase.RecordingControlUseCases
import com.actively.repository.ActivityRecordingRepository
import com.actively.util.TimeProvider
import com.mapbox.geojson.LineString
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecorderViewModel(
    private val recordingControlUseCases: RecordingControlUseCases,
    private val timeProvider: TimeProvider,
    private val activityRecordingRepository: ActivityRecordingRepository,
) : ViewModel() {

    private val _stats = MutableStateFlow(StatisticsState())
    val stats = _stats.asStateFlow()

    val route = activityRecordingRepository.getRoute()
        .toGeoJsonFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    private val _controlsState = MutableStateFlow(
        ControlsState(current = RecorderState.Idle, previous = RecorderState.Idle)
    )
    val controlsState: StateFlow<ControlsState> = _controlsState.asStateFlow()

    private var statsUpdates: Job? = null

    init {
        viewModelScope.launch {
            _stats.update {
                activityRecordingRepository.getStats().first().toState()
            }
            val recordingState = activityRecordingRepository.getState().first()
            if (recordingState is RecorderState.Started) {
                resumeRecording()
            }
        }

        activityRecordingRepository.getState().onEach { newState ->
            _controlsState.update { currentState ->
                ControlsState(current = newState, previous = currentState.current)
            }
        }.launchIn(viewModelScope)
    }

    fun startRecording() = viewModelScope.launch {
        recordingControlUseCases.startRecording("Cycling", timeProvider())
        statsUpdates = launchStatsUpdates()
    }

    fun pauseRecording() {
        recordingControlUseCases.pauseRecording()
        statsUpdates?.cancel()
        statsUpdates = null
    }

    fun resumeRecording() = viewModelScope.launch {
        recordingControlUseCases.resumeRecording(timeProvider())
        statsUpdates = launchStatsUpdates()
    }

    fun stopRecording() = viewModelScope.launch {
        recordingControlUseCases.stopRecording()
        statsUpdates?.cancel()
        statsUpdates = null
    }

    private fun launchStatsUpdates() = activityRecordingRepository.getStats()
        .onEach { stats ->
            _stats.update { stats.toState() }
        }
        .launchIn(viewModelScope)

    private fun Flow<List<RouteSlice>>.toGeoJsonFlow() = mapNotNull { slices ->
        slices.flatMap(RouteSlice::locations)
            .takeIf { it.size >= 2 }
            ?.toGeoJson()
    }

    private fun List<Location>.toGeoJson() = LineString.fromLngLats(map(Location::toPoint)).toJson()
}
