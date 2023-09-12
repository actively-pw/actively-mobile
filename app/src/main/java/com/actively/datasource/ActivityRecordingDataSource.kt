package com.actively.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrDefault
import com.actively.ActivityDatabase
import com.actively.activity.Activity
import com.actively.activity.Location
import com.actively.activity.RouteSlice
import com.actively.distance.Distance.Companion.inMeters
import com.actively.distance.Distance.Companion.meters
import com.actively.recorder.RecorderState
import com.actively.recorder.asString
import com.actively.recorder.toRecorderState
import database.GetRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.milliseconds

interface ActivityRecordingDataSource {

    suspend fun getActivityCount(): Int

    suspend fun getActivity(): Activity?

    fun getStats(): Flow<Activity.Stats>

    suspend fun updateStats(transform: (Activity.Stats) -> Activity.Stats): Activity.Stats

    fun getRoute(): Flow<List<RouteSlice>>

    suspend fun getLatestLocationFromLastRouteSlice(): Location?

    suspend fun insertActivity(
        id: Activity.Id,
        title: String?,
        sport: String,
        stats: Activity.Stats
    )

    suspend fun insertStats(stats: Activity.Stats)

    suspend fun insertEmptyRouteSlice(start: Instant)

    suspend fun insertLocationToLatestRouteSlice(location: Location)

    suspend fun setState(state: RecorderState)

    fun getState(): Flow<RecorderState>
}

class ActivityRecordingDataSourceImpl(
    database: ActivityDatabase,
    private val coroutineContext: CoroutineContext = Dispatchers.IO
) : ActivityRecordingDataSource {

    private val query = database.recordActivityQueries

    override suspend fun getActivityCount() = withContext(coroutineContext) {
        query.getActivityCount().executeAsOne().toInt()
    }

    override suspend fun getActivity(): Activity? = withContext(coroutineContext) {
        query.transactionWithResult {
            val activityWithStatsQuery = query.getActivity()
                .executeAsOneOrNull() ?: return@transactionWithResult null
            val routeQuery = query.getRoute().executeAsList()
            Activity(
                id = Activity.Id(activityWithStatsQuery.uuid),
                title = activityWithStatsQuery.title,
                sport = activityWithStatsQuery.sport,
                stats = Activity.Stats(
                    totalTime = activityWithStatsQuery.totalTime.milliseconds,
                    distance = activityWithStatsQuery.totalDistanceMeters.meters,
                    averageSpeed = activityWithStatsQuery.averageSpeed
                ),
                route = routeQuery.toRouteSlices()
            )
        }
    }

    override fun getStats() = query
        .getActivityStats { _, totalTime, distanceMeters, averageSpeed ->
            Activity.Stats(
                totalTime = totalTime.milliseconds,
                distance = distanceMeters.meters,
                averageSpeed = averageSpeed
            )
        }
        .asFlow()
        .mapToOneOrDefault(Activity.Stats.empty(), coroutineContext)

    override suspend fun updateStats(transform: (Activity.Stats) -> Activity.Stats): Activity.Stats {
        return withContext(coroutineContext) {
            query.transactionWithResult {
                val currentStats = query
                    .getActivityStats { _, totalTime, distanceMeters, averageSpeed ->
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
    }

    override fun getRoute() = query.getRoute()
        .asFlow()
        .mapToList(coroutineContext)
        .map { it.toRouteSlices() }

    override suspend fun getLatestLocationFromLastRouteSlice() =
        withContext(coroutineContext) {
            query.getLatestLocationFromLastRouteSlice { _, timestamp, latitude, longitude ->
                Location(
                    timestamp = Instant.fromEpochMilliseconds(timestamp),
                    latitude = latitude,
                    longitude = longitude
                )
            }.executeAsOneOrNull()
        }

    override suspend fun insertActivity(
        id: Activity.Id,
        title: String?,
        sport: String,
        stats: Activity.Stats
    ) = withContext(coroutineContext) {
        query.transaction {
            query.insertActivity(uuid = id.value, title = title, sport = sport)
            query.insertActivityStats(
                totalTime = stats.totalTime.inWholeMilliseconds,
                totalDistanceMeters = stats.distance.inMeters,
                averageSpeed = stats.averageSpeed
            )
        }
    }

    override suspend fun insertStats(stats: Activity.Stats) =
        withContext(coroutineContext) {
            query.insertActivityStats(
                totalTime = stats.totalTime.inWholeMilliseconds,
                totalDistanceMeters = stats.distance.inMeters,
                averageSpeed = stats.averageSpeed
            )
        }

    override suspend fun insertEmptyRouteSlice(start: Instant) =
        withContext(coroutineContext) {
            query.insertRouteSlice(id = null, start = start.toEpochMilliseconds())
        }

    override suspend fun insertLocationToLatestRouteSlice(location: Location) {
        withContext(coroutineContext) {
            query.insertLocationToLatestRouteSlice(
                latitude = location.latitude,
                longitude = location.longitude,
                timestamp = location.timestamp.toEpochMilliseconds()
            )
        }
    }

    override suspend fun setState(state: RecorderState) {
        withContext(coroutineContext) {
            query.setRecorderState(state.asString())
        }
    }

    override fun getState() =
        query.getRecorderState { _, stateString -> stateString.toRecorderState() }
            .asFlow()
            .mapToOneOrDefault(RecorderState.Idle, coroutineContext)

    private fun List<GetRoute>.toRouteSlices() = groupBy { it.start }
        .map { (start, getRouteQuery) ->
            RouteSlice(
                start = Instant.fromEpochMilliseconds(start),
                locations = getRouteQuery.mapNotNull {
                    if (it.timestamp == null || it.latitude == null || it.longitude == null)
                        return@mapNotNull null
                    Location(
                        timestamp = Instant.fromEpochMilliseconds(it.timestamp),
                        latitude = it.latitude,
                        longitude = it.longitude
                    )
                }
            )
        }
}

