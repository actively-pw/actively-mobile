package com.actively.datasource.factory

import com.actively.datasource.RecordedActivitiesDataSource
import com.actively.datasource.paged.PagedRecordedActivitiesDataSource

interface RecordedActivitiesDataSourceFactory {

    fun create(): PagedRecordedActivitiesDataSource
}

class RecordedActivitiesDataSourceFactoryImpl(
    private val dataSource: RecordedActivitiesDataSource
) : RecordedActivitiesDataSourceFactory {

    override fun create() = PagedRecordedActivitiesDataSource(dataSource)
}
