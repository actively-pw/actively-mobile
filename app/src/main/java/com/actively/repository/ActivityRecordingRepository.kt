package com.actively.repository

import com.actively.activity.Activity
import com.actively.activity.Location
import com.actively.activity.RouteSlice
import com.actively.datasource.ActivityRecordingDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface ActivityRecordingRepository {

    suspend fun getActivity(id: Activity.Id): Activity?

    fun getStats(id: Activity.Id): Flow<Activity.Stats>

    fun getRoute(id: Activity.Id): Flow<List<RouteSlice>>

    suspend fun getLatestRouteLocation(id: Activity.Id): Location?

    suspend fun insertRoutelessActivity(activity: Activity)

    suspend fun insertStats(stats: Activity.Stats, id: Activity.Id)

    suspend fun insertEmptyRouteSlice(id: Activity.Id, start: Instant)

    suspend fun insertLocation(location: Location, id: Activity.Id)
}

class ActivityRecordingRepositoryImpl(
    private val activityRecordingDataSource: ActivityRecordingDataSource
) : ActivityRecordingRepository {


    override suspend fun getActivity(id: Activity.Id) = activityRecordingDataSource.getActivity(id)

    override fun getStats(id: Activity.Id) = activityRecordingDataSource.getStats(id)

    override fun getRoute(id: Activity.Id) = activityRecordingDataSource.getRoute(id)

    override suspend fun getLatestRouteLocation(id: Activity.Id) = activityRecordingDataSource
        .getLatestLocationFromLastRouteSlice(id)

    override suspend fun insertRoutelessActivity(activity: Activity) = activityRecordingDataSource
        .insertActivity(activity.id, activity.title, activity.sport, activity.stats)

    override suspend fun insertStats(stats: Activity.Stats, id: Activity.Id) =
        activityRecordingDataSource.insertStats(stats, id)

    override suspend fun insertEmptyRouteSlice(id: Activity.Id, start: Instant) =
        activityRecordingDataSource.insertEmptyRouteSlice(id, start)

    override suspend fun insertLocation(location: Location, id: Activity.Id) =
        activityRecordingDataSource.insertLocationToLatestRouteSlice(id, location)

}
