package com.actively.recorder.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SaveActivityViewModel : ViewModel() {

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
        //todo: delete current activity
    }

    fun onSaveClick() = viewModelScope.launch {
        //todo: save current activity
    }

    fun onBackClick() = viewModelScope.launch {
        //todo: navigate back to recorder screen
    }
}
