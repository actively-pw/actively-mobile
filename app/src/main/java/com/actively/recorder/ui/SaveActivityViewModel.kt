package com.actively.recorder.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.actively.recorder.usecase.StopRecordingUseCase
import com.actively.repository.ActivityRecordingRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SaveActivityViewModel(
    private val stopRecordingUseCase: StopRecordingUseCase,
    private val activityRecordingRepository: ActivityRecordingRepository,
) : ViewModel() {

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _showDiscardDialog = MutableSharedFlow<Boolean>()
    val showDiscardDialog = _showDiscardDialog.asSharedFlow()

    fun onTitleChange(title: String) {
        _title.update { title }
    }

    fun onDiscardClick() = viewModelScope.launch {
        _showDiscardDialog.emit(true)
    }

    fun onDismissDialog() = viewModelScope.launch {
        _showDiscardDialog.emit(false)
    }

    fun onConfirmDiscard() = viewModelScope.launch {
        _showDiscardDialog.emit(false)
        activityRecordingRepository.removeRecordingActivity()
    }

    fun onSaveClick() = viewModelScope.launch {
        stopRecordingUseCase(_title.value)
    }
}
