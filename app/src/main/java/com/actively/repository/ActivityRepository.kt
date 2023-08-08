package com.actively.repository

import com.actively.activity.Activity
import com.actively.activity.Location
import com.actively.datasource.ActivityDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

interface ActivityRepository {

    fun getActivities(): Flow<List<Activity>>

    suspend fun getActivity(id: Activity.Id): Activity?

    suspend fun getStats(id: Activity.Id): Activity.Stats?

    suspend fun getRoute(id: Activity.Id): List<Location>

    suspend fun insertActivity(activity: Activity)

    suspend fun insertStats(stats: Activity.Stats, id: Activity.Id)

    suspend fun insertLocation(location: Location, id: Activity.Id)
}

class ActivityRepositoryImpl(
    private val activityDataSource: ActivityDataSource,
    private val coroutineContext: CoroutineContext = Dispatchers.IO
) : ActivityRepository {

    override fun getActivities() = activityDataSource.getActivities()

    override suspend fun getActivity(id: Activity.Id) = withContext(coroutineContext) {
        activityDataSource.getActivity(id)
    }

    override suspend fun getStats(id: Activity.Id) = withContext(coroutineContext) {
        activityDataSource.getStats(id)
    }

    override suspend fun getRoute(id: Activity.Id) = withContext(coroutineContext) {
        activityDataSource.getRoute(id)
    }

    override suspend fun insertActivity(activity: Activity) = withContext(coroutineContext) {
        activityDataSource.insertActivity(activity)
    }

    override suspend fun insertStats(stats: Activity.Stats, id: Activity.Id) {
        withContext(coroutineContext) {
            activityDataSource.insertStats(stats, id)
        }
    }

    override suspend fun insertLocation(location: Location, id: Activity.Id) {
        withContext(coroutineContext) {
            activityDataSource.insertLocation(location, id)
        }
    }
}
