package com.actively.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.actively.ActivityDatabase
import com.actively.activity.Activity
import com.actively.activity.Location
import database.ActivityEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.milliseconds

interface ActivityDataSource {

    fun getActivities(): Flow<List<Activity>>

    fun getActivity(id: Activity.Id): Activity?

    fun getStats(id: Activity.Id): Activity.Stats?

    fun getRoute(id: Activity.Id): List<Location>

    fun insertActivity(activity: Activity)

    fun insertStats(stats: Activity.Stats, id: Activity.Id)

    fun insertRoute(route: List<Location>, id: Activity.Id)

    fun insertLocation(location: Location, id: Activity.Id)
}

class ActivityDataSourceImpl(
    database: ActivityDatabase,
    private val coroutineContext: CoroutineContext = Dispatchers.IO
) : ActivityDataSource {

    private val query = database.activityQueries

    override fun getActivities() = query.getActivities()
        .asFlow()
        .mapToList(coroutineContext)
        .map {
            withContext(coroutineContext) { it.toActivities() }
        }

    override fun getActivity(id: Activity.Id): Activity? = query.transactionWithResult {
        val activity = query.getActivity(id = id.value)
            .executeAsOneOrNull() ?: return@transactionWithResult null
        Activity(
            id = id,
            title = activity.title,
            sport = activity.sport,
            start = Instant.fromEpochMilliseconds(activity.start),
            stats = getStats(id) ?: return@transactionWithResult null,
            route = getRoute(id)
        )
    }

    override fun getStats(id: Activity.Id) = query
        .getActivityStats(activityId = id.value, mapper = ::toActivityStats)
        .executeAsOneOrNull()

    override fun getRoute(id: Activity.Id) = query.getRoute(activityId = id.value, ::toLocation)
        .executeAsList()

    override fun insertActivity(activity: Activity) = query.transaction {
        with(activity) {
            query.insertActivity(
                id = id.value,
                title = title,
                sport = sport,
                start = start.toEpochMilliseconds()
            )
            insertStats(stats = stats, id = id)
            insertRoute(route = route, id = id)
        }
    }

    override fun insertStats(stats: Activity.Stats, id: Activity.Id) = query.insertActivityStats(
        activityId = id.value,
        totalTime = stats.totalTime.inWholeMilliseconds,
        totalDistance = stats.distance,
        averageSpeed = stats.averageSpeed
    )

    override fun insertRoute(route: List<Location>, id: Activity.Id) = query.transaction {
        route.forEach { insertLocation(it, id = id) }
    }

    override fun insertLocation(location: Location, id: Activity.Id) = query.insertLocation(
        activityId = id.value,
        latitude = location.latitude,
        longitute = location.longitude,
        timestamp = location.timestamp.toEpochMilliseconds(),
    )

    private fun List<ActivityEntity>.toActivities() = query.transactionWithResult {
        mapNotNull { activity ->
            val id = Activity.Id(activity.id)
            Activity(
                id = id,
                title = activity.title,
                sport = activity.sport,
                start = Instant.fromEpochMilliseconds(activity.start),
                stats = getStats(id) ?: return@mapNotNull null,
                route = getRoute(id)
            )
        }
    }

    private fun toActivityStats(
        activityId: String,
        totalTime: Long,
        distance: Double,
        averageSpeed: Double
    ) = Activity.Stats(
        totalTime = totalTime.milliseconds,
        distance = distance,
        averageSpeed = averageSpeed
    )

    private fun toLocation(
        activityId: String,
        timestamp: Long,
        longitude: Double,
        latitude: Double
    ) = Location(
        longitude = longitude,
        latitude = latitude,
        timestamp = Instant.fromEpochMilliseconds(timestamp)
    )
}

