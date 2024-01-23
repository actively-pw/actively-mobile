package com.actively.datasource.factory

import com.actively.datasource.paged.PagedRecordedActivitiesDataSource
import com.actively.repository.RecordedActivitiesRepository

interface RecordedActivitiesDataSourceFactory {

    fun create(): PagedRecordedActivitiesDataSource
}

class RecordedActivitiesDataSourceFactoryImpl(
    private val dataSource: RecordedActivitiesRepository
) : RecordedActivitiesDataSourceFactory {

    override fun create() = PagedRecordedActivitiesDataSource(dataSource)
}
