package com.actively.home.ui

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.actively.datasource.RecordedActivitiesDataSource

class HomeViewModel(
    private val recordedActivitiesDataSource: RecordedActivitiesDataSource
) : ViewModel() {

    val activitiesPager = Pager(config = PagingConfig(pageSize = 5)) {
        recordedActivitiesDataSource
    }
}
