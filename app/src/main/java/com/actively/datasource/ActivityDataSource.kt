package com.actively.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.actively.ActivityDatabase
import com.actively.activity.Activity
import com.actively.activity.Location
import com.actively.distance.Distance.Companion.inMeters
import com.actively.distance.Distance.Companion.meters
import database.ActivityEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.milliseconds

interface ActivityDataSource {

    fun getActivities(): Flow<List<Activity>>

    suspend fun getActivity(id: Activity.Id): Activity?

    fun getStats(id: Activity.Id): Flow<Activity.Stats>

    fun getRoute(id: Activity.Id): Flow<List<Location>>

    suspend fun getLatestLocation(id: Activity.Id): Location?

    suspend fun insertActivity(activity: Activity)

    suspend fun insertStats(stats: Activity.Stats, id: Activity.Id)

    suspend fun insertRoute(route: List<Location>, id: Activity.Id)

    suspend fun insertLocation(location: Location, id: Activity.Id)
}

class ActivityDataSourceImpl(
    database: ActivityDatabase,
    private val coroutineContext: CoroutineContext = Dispatchers.IO
) : ActivityDataSource {

    private val query = database.activityQueries

    override fun getActivities() = query.getActivities()
        .asFlow()
        .mapToList(coroutineContext)
        .map { it.toActivities() }
        .flowOn(coroutineContext)

    override suspend fun getActivity(id: Activity.Id): Activity? = withContext(coroutineContext) {
        query.transactionWithResult {
            val activity = query.getActivity(id = id.value)
                .executeAsOneOrNull() ?: return@transactionWithResult null
            Activity(
                id = id,
                title = activity.title,
                sport = activity.sport,
                start = Instant.fromEpochMilliseconds(activity.start),
                stats = getStatsBlocking(id) ?: return@transactionWithResult null,
                route = getRouteBlocking(id)
            )
        }
    }

    override fun getStats(id: Activity.Id) = query
        .getActivityStats(activityId = id.value, mapper = ::toActivityStats)
        .asFlow()
        .mapToOne(coroutineContext)

    override fun getRoute(id: Activity.Id) = query.getRoute(activityId = id.value, ::toLocation)
        .asFlow()
        .mapToList(coroutineContext)

    override suspend fun getLatestLocation(id: Activity.Id) = withContext(coroutineContext) {
        query.getLatestRouteLocation(activityId = id.value, ::toLocation).executeAsOneOrNull()
    }

    override suspend fun insertActivity(activity: Activity) = withContext(coroutineContext) {
        query.transaction {
            with(activity) {
                query.insertActivity(
                    id = id.value,
                    title = title,
                    sport = sport,
                    start = start.toEpochMilliseconds()
                )
                insertStatsBlocking(stats = stats, id = id)
                insertRouteBlocking(route = route, id = id)
            }
        }
    }

    override suspend fun insertStats(stats: Activity.Stats, id: Activity.Id) =
        withContext(coroutineContext) {
            query.insertActivityStats(
                activityId = id.value,
                totalTime = stats.totalTime.inWholeMilliseconds,
                totalDistance = stats.distance.inMeters,
                averageSpeed = stats.averageSpeed
            )
        }

    override suspend fun insertRoute(route: List<Location>, id: Activity.Id) =
        withContext(coroutineContext) {
            query.transaction {
                route.forEach { insertLocationBlocking(it, id = id) }
            }
        }

    override suspend fun insertLocation(location: Location, id: Activity.Id) =
        withContext(coroutineContext) {
            query.insertLocation(
                activityId = id.value,
                latitude = location.latitude,
                longitute = location.longitude,
                timestamp = location.timestamp.toEpochMilliseconds(),
            )
        }

    private fun List<ActivityEntity>.toActivities() = query.transactionWithResult {
        mapNotNull { activity ->
            val id = Activity.Id(activity.id)
            Activity(
                id = id,
                title = activity.title,
                sport = activity.sport,
                start = Instant.fromEpochMilliseconds(activity.start),
                stats = getStatsBlocking(id) ?: return@mapNotNull null,
                route = getRouteBlocking(id)
            )
        }
    }

    private fun getStatsBlocking(id: Activity.Id) =
        query.getActivityStats(id.value, ::toActivityStats)
            .executeAsOneOrNull()


    private fun getRouteBlocking(id: Activity.Id) = query.getRoute(id.value, ::toLocation)
        .executeAsList()

    private fun insertStatsBlocking(stats: Activity.Stats, id: Activity.Id) =
        query.insertActivityStats(
            activityId = id.value,
            totalTime = stats.totalTime.inWholeMilliseconds,
            totalDistance = stats.distance.inMeters,
            averageSpeed = stats.averageSpeed
        )

    private fun insertRouteBlocking(route: List<Location>, id: Activity.Id) = query.transaction {
        route.forEach { insertLocationBlocking(it, id = id) }
    }

    private fun insertLocationBlocking(location: Location, id: Activity.Id) = query.insertLocation(
        activityId = id.value,
        latitude = location.latitude,
        longitute = location.longitude,
        timestamp = location.timestamp.toEpochMilliseconds(),
    )

    private fun toActivityStats(
        activityId: String,
        totalTime: Long,
        distance: Double,
        averageSpeed: Double
    ) = Activity.Stats(
        totalTime = totalTime.milliseconds,
        distance = distance.meters,
        averageSpeed = averageSpeed
    )

    private fun toLocation(
        activityId: String,
        timestamp: Long,
        latitude: Double,
        longitude: Double
    ) = Location(
        longitude = longitude,
        latitude = latitude,
        timestamp = Instant.fromEpochMilliseconds(timestamp)
    )
}

