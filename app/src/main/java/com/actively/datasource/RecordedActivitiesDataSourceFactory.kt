package com.actively.datasource

import com.actively.datasource.paged.PagedRecordedActivitiesDataSource
import io.ktor.client.HttpClient

interface RecordedActivitiesDataSourceFactory {

    fun create(): PagedRecordedActivitiesDataSource
}

class RecordedActivitiesDataSourceFactoryImpl(
    private val client: HttpClient
) : RecordedActivitiesDataSourceFactory {

    override fun create() = PagedRecordedActivitiesDataSource(client)
}
