package com.actively.datasource

import io.ktor.client.HttpClient

interface RecordedActivitiesDataSourceFactory {

    fun create(): RecordedActivitiesDataSource
}

class RecordedActivitiesDataSourceFactoryImpl(
    private val client: HttpClient
) : RecordedActivitiesDataSourceFactory {

    override fun create() = RecordedActivitiesDataSource(client)
}
