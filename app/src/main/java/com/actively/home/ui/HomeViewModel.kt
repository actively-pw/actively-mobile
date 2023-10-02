package com.actively.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.actively.datasource.RecordedActivitiesDataSourceFactory
import com.actively.synchronizer.usecases.GetSyncStateUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    getSyncStateUseCase: GetSyncStateUseCase,
    private val recordedActivitiesDataSourceFactory: RecordedActivitiesDataSourceFactory,
) : ViewModel() {

    val activitiesPager = Pager(config = PagingConfig(pageSize = 5)) {
        recordedActivitiesDataSourceFactory.create()
    }

    val syncState = getSyncStateUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
}
