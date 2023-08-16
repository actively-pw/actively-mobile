package com.actively.repository

import com.actively.activity.Activity
import com.actively.activity.Location
import com.actively.datasource.ActivityDataSource
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {

    fun getActivities(): Flow<List<Activity>>

    suspend fun getActivity(id: Activity.Id): Activity?

    fun getStats(id: Activity.Id): Flow<Activity.Stats>

    fun getRoute(id: Activity.Id): Flow<List<Location>>

    suspend fun getLatestRouteLocation(id: Activity.Id): Location?

    suspend fun insertActivity(activity: Activity)

    suspend fun insertStats(stats: Activity.Stats, id: Activity.Id)

    suspend fun insertLocation(location: Location, id: Activity.Id)
}

class ActivityRepositoryImpl(
    private val activityDataSource: ActivityDataSource
) : ActivityRepository {

    override fun getActivities() = activityDataSource.getActivities()

    override suspend fun getActivity(id: Activity.Id) = activityDataSource.getActivity(id)

    override fun getStats(id: Activity.Id) = activityDataSource.getStats(id)


    override fun getRoute(id: Activity.Id) = activityDataSource.getRoute(id)


    override suspend fun getLatestRouteLocation(id: Activity.Id) =
        activityDataSource.getLatestLocation(id)


    override suspend fun insertActivity(activity: Activity) =
        activityDataSource.insertActivity(activity)


    override suspend fun insertStats(stats: Activity.Stats, id: Activity.Id) =
        activityDataSource.insertStats(stats, id)


    override suspend fun insertLocation(location: Location, id: Activity.Id) =
        activityDataSource.insertLocation(location, id)

}
