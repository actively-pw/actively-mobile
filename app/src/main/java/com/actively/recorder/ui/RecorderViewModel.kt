package com.actively.recorder.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.actively.activity.Discipline
import com.actively.activity.Location
import com.actively.activity.RouteSlice
import com.actively.recorder.RecorderState
import com.actively.recorder.usecase.RecordingControlUseCases
import com.actively.repository.ActivityRecordingRepository
import com.actively.util.TimeProvider
import com.mapbox.geojson.LineString
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecorderViewModel(
    private val recordingControlUseCases: RecordingControlUseCases,
    private val timeProvider: TimeProvider,
    private val activityRecordingRepository: ActivityRecordingRepository,
) : ViewModel() {

    private val _disciplineState = MutableStateFlow(DisciplineState())
    val disciplineState = _disciplineState.asStateFlow()

    private val _stats = MutableStateFlow(StatisticsState())
    val stats = _stats.asStateFlow()

    private val _route = MutableStateFlow<String?>(null)
    val route = _route.asStateFlow()

    private val _controlsState = MutableStateFlow(
        ControlsState(current = RecorderState.Idle, previous = RecorderState.Idle)
    )
    val controlsState: StateFlow<ControlsState> = _controlsState.asStateFlow()

    private val _showPermissionRequestDialog = MutableSharedFlow<Boolean>()
    val showPermissionRequestDialog = _showPermissionRequestDialog.asSharedFlow()

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

        activityRecordingRepository.getRoute()
            .toGeoJsonFlow()
            .onEach { geoJson -> _route.update { geoJson } }
            .launchIn(viewModelScope)

        activityRecordingRepository.getState().onEach { newState ->
            _controlsState.update { currentState ->
                ControlsState(current = newState, previous = currentState.current)
            }
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            activityRecordingRepository.getState().firstOrNull()?.let { recorderState ->
                if (recorderState is RecorderState.Idle) {
                    _disciplineState.update { it.copy(showSelectSportButton = true) }
                }
            }
        }
    }

    fun startRecording() = viewModelScope.launch {
        _disciplineState.update { it.copy(showSelectSportButton = false) }
        recordingControlUseCases.startRecording(
            _disciplineState.value.selectedDiscipline,
            timeProvider()
        )
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

    fun showRequestPermissionDialog() = viewModelScope.launch {
        _showPermissionRequestDialog.emit(true)
    }

    fun dismissRequestPermissionDialog() = viewModelScope.launch {
        _showPermissionRequestDialog.emit(false)
    }

    fun onShowBottomSheet() = _disciplineState.update { it.copy(showBottomSheet = true) }

    fun onHideBottomSheet() = _disciplineState.update { it.copy(showBottomSheet = false) }

    fun selectDiscipline(discipline: Discipline) {
        _disciplineState.update { it.copy(selectedDiscipline = discipline) }
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
