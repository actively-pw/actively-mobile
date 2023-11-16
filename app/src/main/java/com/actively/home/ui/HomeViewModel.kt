package com.actively.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import com.actively.auth.usecases.LogOutUseCase
import com.actively.datasource.factory.RecordedActivitiesDataSourceFactory
import com.actively.synchronizer.usecases.GetSyncStateUseCase
import com.actively.util.TimeProvider
import com.actively.util.getActivityTimeString
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    getSyncStateUseCase: GetSyncStateUseCase,
    private val timeProvider: TimeProvider,
    private val recordedActivitiesDataSourceFactory: RecordedActivitiesDataSourceFactory,
    private val logOutUseCase: LogOutUseCase,
) : ViewModel() {

    val activitiesPager = Pager(config = PagingConfig(pageSize = 10)) {
        recordedActivitiesDataSourceFactory.create()
    }.flow.map { pagingData ->
        pagingData.map {
            RecordedActivityUi(
                id = it.id,
                title = it.title,
                time = getActivityTimeString(start = it.start, now = timeProvider()),
                stats = it.stats,
                mapUrl = it.mapUrl
            )
        }
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
