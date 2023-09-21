package com.actively.repository

import com.actively.activity.Activity
import com.actively.activity.Location
import com.actively.activity.RouteSlice
import com.actively.datasource.ActivityRecordingDataSource
import com.actively.datasource.SyncActivitiesDataSource
import com.actively.recorder.RecorderState
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface ActivityRecordingRepository {

    //todo: no use
    suspend fun isActivityPresent(): Boolean

    suspend fun getActivity(id: Activity.Id): Activity?

    fun getStats(): Flow<Activity.Stats>

    suspend fun updateStats(transform: (Activity.Stats) -> Activity.Stats): Activity.Stats

    fun getRoute(): Flow<List<RouteSlice>>

    suspend fun getLatestRouteLocation(): Location?

    suspend fun insertRoutelessActivity(activity: Activity)

    suspend fun insertStats(stats: Activity.Stats)

    suspend fun insertEmptyRouteSlice(start: Instant)

    suspend fun insertLocation(location: Location)

    suspend fun setState(state: RecorderState)

    fun getState(): Flow<RecorderState>

    suspend fun markActivityAsRecorded()

    suspend fun getRecordedActivitiesId(): List<Activity.Id>

    suspend fun removeActivity(id: Activity.Id)

    suspend fun syncActivity(activity: Activity)

    suspend fun removeRecordingActivity()

    suspend fun updateRecordingActivityTitle(title: String)
}

class ActivityRecordingRepositoryImpl(
    private val activityRecordingDataSource: ActivityRecordingDataSource,
    private val syncActivitiesDataSource: SyncActivitiesDataSource,
) : ActivityRecordingRepository {

    override suspend fun isActivityPresent() = activityRecordingDataSource.getActivityCount() == 1

    override suspend fun getActivity(id: Activity.Id) = activityRecordingDataSource.getActivity(id)

    override fun getStats() = activityRecordingDataSource.getStats()

    override suspend fun updateStats(transform: (Activity.Stats) -> Activity.Stats): Activity.Stats {
        return activityRecordingDataSource.updateStats(transform)
    }

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

    override suspend fun setState(state: RecorderState) = activityRecordingDataSource
        .setState(state)

    override fun getState() = activityRecordingDataSource.getState()

    override suspend fun markActivityAsRecorded() =
        activityRecordingDataSource.markActivityAsRecorded()

    override suspend fun getRecordedActivitiesId() =
        activityRecordingDataSource.getRecordedActivitiesId()

    override suspend fun removeActivity(id: Activity.Id) =
        activityRecordingDataSource.removeActivity(id)

    override suspend fun syncActivity(activity: Activity) =
        syncActivitiesDataSource.syncActivity(activity)

    override suspend fun removeRecordingActivity() {
        activityRecordingDataSource.removeRecordingActivity()
    }

    override suspend fun updateRecordingActivityTitle(title: String) {
        activityRecordingDataSource.updateRecordingActivityTitle(title)
    }
}
