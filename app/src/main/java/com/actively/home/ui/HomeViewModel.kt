package com.actively.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.actively.datasource.RecordedActivitiesDataSource
import com.actively.synchronizer.usecases.GetSyncStateUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    getSyncStateUseCase: GetSyncStateUseCase,
    private val recordedActivitiesDataSource: RecordedActivitiesDataSource,
) : ViewModel() {

    val activitiesPager = Pager(config = PagingConfig(pageSize = 5)) {
        recordedActivitiesDataSource
    }

    val syncState = getSyncStateUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
}
