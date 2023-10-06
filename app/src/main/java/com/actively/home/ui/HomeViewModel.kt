package com.actively.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.actively.auth.usecases.LogOutUseCase
import com.actively.datasource.factory.RecordedActivitiesDataSourceFactory
import com.actively.synchronizer.usecases.GetSyncStateUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    getSyncStateUseCase: GetSyncStateUseCase,
    private val recordedActivitiesDataSourceFactory: RecordedActivitiesDataSourceFactory,
    private val logOutUseCase: LogOutUseCase,
) : ViewModel() {

    val activitiesPager = Pager(config = PagingConfig(pageSize = 5)) {
        recordedActivitiesDataSourceFactory.create()
    }

    val syncState = getSyncStateUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun onLogout(block: () -> Unit) {
        viewModelScope.launch {
            logOutUseCase()
            block()
        }
    }
}
