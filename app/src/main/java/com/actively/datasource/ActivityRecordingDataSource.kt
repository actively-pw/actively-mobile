package com.actively.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrDefault
import com.actively.ActivityDatabase
import com.actively.activity.Activity
import com.actively.activity.Discipline
import com.actively.activity.Location
import com.actively.activity.RouteSlice
import com.actively.activity.asString
import com.actively.activity.toDiscipline
import com.actively.distance.Distance.Companion.inMeters
import com.actively.distance.Distance.Companion.meters
import com.actively.recorder.RecorderState
import com.actively.recorder.asString
import com.actively.recorder.toRecorderState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.milliseconds

interface ActivityRecordingDataSource {

    suspend fun getActivity(id: Activity.Id): Activity?

    fun getStats(): Flow<Activity.Stats>

    suspend fun updateStats(transform: (Activity.Stats) -> Activity.Stats): Activity.Stats

    fun getRoute(): Flow<List<RouteSlice>>

    suspend fun getLatestLocationFromLastRouteSlice(): Location?

    suspend fun insertActivity(
        id: Activity.Id,
        title: String?,
        sport: Discipline,
        stats: Activity.Stats
    )

    suspend fun insertEmptyRouteSlice(start: Instant)

    suspend fun insertLocationToLatestRouteSlice(location: Location)

    suspend fun setState(state: RecorderState)

    fun getState(): Flow<RecorderState>

    suspend fun markActivityAsRecorded()

    suspend fun removeActivity(id: Activity.Id)

    suspend fun getRecordedActivitiesId(): List<Activity.Id>

    suspend fun removeRecordingActivity()

    suspend fun getDiscipline(): Discipline?

    suspend fun updateRecordingActivityTitle(title: String)

    suspend fun clearDatabase()
}

class ActivityRecordingDataSourceImpl(
    database: ActivityDatabase,
    private val coroutineContext: CoroutineContext = Dispatchers.IO
) : ActivityRecordingDataSource {

    private val query = database.recordActivityQueries

    override suspend fun getActivity(id: Activity.Id): Activity? =
        query.suspendingTransactionWithResult(coroutineContext) {
            val activityWithStatsQuery = query.getActivity(id.value)
                .executeAsOneOrNull() ?: return@suspendingTransactionWithResult null
            val routeQuery = query
                .getRouteByActivityId(id.value, ::RouteQuery)
                .executeAsList()
            Activity(
                id = Activity.Id(activityWithStatsQuery.uuid),
                title = activityWithStatsQuery.title,
                sport = activityWithStatsQuery.sport.toDiscipline(),
                stats = Activity.Stats(
                    totalTime = activityWithStatsQuery.totalTime.milliseconds,
                    distance = activityWithStatsQuery.totalDistanceMeters.meters,
                    averageSpeed = activityWithStatsQuery.averageSpeed
                ),
                route = routeQuery.toRouteSlices()
            )
        }

    override fun getStats() = query
        .getActivityStats { totalTime, distanceMeters, averageSpeed ->
            Activity.Stats(
                totalTime = totalTime.milliseconds,
                distance = distanceMeters.meters,
                averageSpeed = averageSpeed
            )
        }
        .asFlow()
        .mapToOneOrDefault(Activity.Stats.empty(), coroutineContext)

    override suspend fun updateStats(transform: (Activity.Stats) -> Activity.Stats): Activity.Stats {
        return query.suspendingTransactionWithResult(coroutineContext) {
            val currentStats = query
                .getActivityStats { totalTime, distanceMeters, averageSpeed ->
                    Activity.Stats(
                        totalTime = totalTime.milliseconds,
                        distance = distanceMeters.meters,
                        averageSpeed = averageSpeed
                    )
                }.executeAsOneOrNull() ?: Activity.Stats.empty()
            transform(currentStats).also {
                query.insertActivityStats(
                    it.totalTime.inWholeMilliseconds,
                    it.distance.inMeters,
                    it.averageSpeed
                )
            }
        }
    }

    override fun getRoute() = query.getRecordingRoute(::RouteQuery)
        .asFlow()
        .mapToList(coroutineContext)
        .map { it.toRouteSlices() }

    override suspend fun getLatestLocationFromLastRouteSlice() = withContext(coroutineContext) {
        query.getLatestLocationFromLastRouteSlice { _, timestamp, latitude, longitude, altitude ->
            Location(
                timestamp = Instant.fromEpochMilliseconds(timestamp),
                latitude = latitude,
                longitude = longitude,
                altitude = altitude
            )
        }.executeAsOneOrNull()
    }

    override suspend fun insertActivity(
        id: Activity.Id,
        title: String?,
        sport: Discipline,
        stats: Activity.Stats
    ) = query.suspendingTransaction(coroutineContext) {
        query.insertActivity(uuid = id.value, title = title, sport = sport.asString())
        query.insertActivityStats(
            totalTime = stats.totalTime.inWholeMilliseconds,
            totalDistanceMeters = stats.distance.inMeters,
            averageSpeed = stats.averageSpeed
        )

    }

    override suspend fun insertEmptyRouteSlice(start: Instant) = withContext(coroutineContext) {
        query.insertRouteSlice(id = null, start = start.toEpochMilliseconds())
    }

    override suspend fun insertLocationToLatestRouteSlice(location: Location) {
        withContext(coroutineContext) {
            query.insertLocationToLatestRouteSlice(
                latitude = location.latitude,
                longitude = location.longitude,
                timestamp = location.timestamp.toEpochMilliseconds(),
                altitude = location.altitude
            )
        }
    }

    override suspend fun setState(state: RecorderState) = withContext(coroutineContext) {
        query.setRecorderState(state.asString())
    }

    override fun getState() =
        query.getRecorderState { _, stateString -> stateString.toRecorderState() }
            .asFlow()
            .mapToOneOrDefault(RecorderState.Idle, coroutineContext)

    override suspend fun markActivityAsRecorded() = withContext(coroutineContext) {
        query.markActivityAsRecorded()
    }

    override suspend fun removeActivity(id: Activity.Id) = withContext(coroutineContext) {
        query.removeActivity(id.value)
    }

    override suspend fun getRecordedActivitiesId() = withContext(coroutineContext) {
        query.getRecordedActivitiesId().executeAsList().map(Activity::Id)
    }

    override suspend fun removeRecordingActivity() = withContext(coroutineContext) {
        query.removeActivityThatIsBeingRecorded()
    }

    override suspend fun getDiscipline() = withContext(coroutineContext) {
        query.getDiscipline().executeAsOneOrNull()?.toDiscipline()
    }

    override suspend fun updateRecordingActivityTitle(title: String) {
        query.updateRecordingActivityTitle(title)
    }

    override suspend fun clearDatabase() {
        query.clearDatabase()
    }

    private fun List<RouteQuery>.toRouteSlices() = groupBy { it.start }
        .map { (start, getRouteQuery) ->
            RouteSlice(
                start = Instant.fromEpochMilliseconds(start),
                locations = getRouteQuery.mapNotNull {
                    if (it.timestamp == null || it.latitude == null || it.longitude == null || it.altitude == null)
                        return@mapNotNull null
                    Location(
                        timestamp = Instant.fromEpochMilliseconds(it.timestamp),
                        latitude = it.latitude,
                        longitude = it.longitude,
                        altitude = it.altitude,
                    )
                }
            )
        }
}

