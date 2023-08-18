package com.actively.repository

import com.actively.activity.Activity
import com.actively.activity.Location
import com.actively.datasource.ActivityRecordingDataSource
import kotlinx.coroutines.flow.Flow

interface ActivityRecordingRepository {

    fun getActivities(): Flow<List<Activity>>

    suspend fun getActivity(id: Activity.Id): Activity?

    fun getStats(id: Activity.Id): Flow<Activity.Stats>

    fun getRoute(id: Activity.Id): Flow<List<Location>>

    suspend fun getLatestRouteLocation(id: Activity.Id): Location?

    suspend fun insertActivity(activity: Activity)

    suspend fun insertStats(stats: Activity.Stats, id: Activity.Id)

    suspend fun insertLocation(location: Location, id: Activity.Id)
}

class ActivityRecordingRepositoryImpl(
    private val activityRecordingDataSource: ActivityRecordingDataSource
) : ActivityRecordingRepository {

    override fun getActivities() = activityRecordingDataSource.getActivities()

    override suspend fun getActivity(id: Activity.Id) = activityRecordingDataSource.getActivity(id)

    override fun getStats(id: Activity.Id) = activityRecordingDataSource.getStats(id)


    override fun getRoute(id: Activity.Id) = activityRecordingDataSource.getRoute(id)


    override suspend fun getLatestRouteLocation(id: Activity.Id) =
        activityRecordingDataSource.getLatestLocation(id)


    override suspend fun insertActivity(activity: Activity) =
        activityRecordingDataSource.insertActivity(activity)


    override suspend fun insertStats(stats: Activity.Stats, id: Activity.Id) =
        activityRecordingDataSource.insertStats(stats, id)


    override suspend fun insertLocation(location: Location, id: Activity.Id) =
        activityRecordingDataSource.insertLocation(location, id)

}
