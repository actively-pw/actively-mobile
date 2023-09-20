package com.actively.recorder.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SaveActivityViewModel : ViewModel() {

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    fun onTitleChange(title: String) {
        _title.update { title }
    }
}
