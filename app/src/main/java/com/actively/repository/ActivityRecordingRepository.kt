package com.actively.repository

import com.actively.activity.Activity
import com.actively.activity.Location
import com.actively.activity.RouteSlice
import com.actively.datasource.ActivityRecordingDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface ActivityRecordingRepository {

    suspend fun isActivityPresent(): Boolean

    suspend fun getActivity(): Activity?

    fun getStats(): Flow<Activity.Stats>

    fun getRoute(): Flow<List<RouteSlice>>

    suspend fun getLatestRouteLocation(): Location?

    suspend fun insertRoutelessActivity(activity: Activity)

    suspend fun insertStats(stats: Activity.Stats)

    suspend fun insertEmptyRouteSlice(start: Instant)

    suspend fun insertLocation(location: Location)
}

class ActivityRecordingRepositoryImpl(
    private val activityRecordingDataSource: ActivityRecordingDataSource
) : ActivityRecordingRepository {

    override suspend fun isActivityPresent() = activityRecordingDataSource.getActivityCount() == 1

    override suspend fun getActivity() = activityRecordingDataSource.getActivity()

    override fun getStats() = activityRecordingDataSource.getStats()

    override fun getRoute() = activityRecordingDataSource.getRoute()

    override suspend fun getLatestRouteLocation() = activityRecordingDataSource
        .getLatestLocationFromLastRouteSlice()

    override suspend fun insertRoutelessActivity(activity: Activity) = activityRecordingDataSource
        .insertActivity(activity.id, activity.title, activity.sport, activity.stats)

    override suspend fun insertStats(stats: Activity.Stats) =
        activityRecordingDataSource.insertStats(stats)

    override suspend fun insertEmptyRouteSlice(start: Instant) =
        activityRecordingDataSource.insertEmptyRouteSlice(start)

    override suspend fun insertLocation(location: Location) = activityRecordingDataSource
        .insertLocationToLatestRouteSlice(location)
}
