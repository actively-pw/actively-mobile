package com.actively.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.actively.ActivityDatabase
import com.actively.activity.Activity
import com.actively.activity.Location
import com.actively.activity.Route
import com.actively.database.toActivity
import com.actively.database.toActivityList
import com.actively.database.toLocation
import com.actively.database.toRoute
import database.ActivityEntity
import database.LocationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.CoroutineContext

interface ActivityDataSource {

    suspend fun getActivities(): Flow<List<Activity>>

    suspend fun getActivity(id: Activity.Id): Activity?

    suspend fun insertActivity(activity: Activity)

    suspend fun getRoute(id: Activity.Id): Route?

    suspend fun insertRoute(route: Route)

    suspend fun getRouteLocations(id: Route.Id): List<Location>

    suspend fun insertLocation(location: Location, routeId: Route.Id)
}

class ActivityDataSourceImpl(
    database: ActivityDatabase,
    private val coroutineContext: CoroutineContext = Dispatchers.IO
) : ActivityDataSource {

    private val query = database.activityQueries

    override suspend fun getActivities() = query.getAllActivities()
        .asFlow()
        .mapToList(coroutineContext)
        .map(List<ActivityEntity>::toActivityList)

    override suspend fun getActivity(id: Activity.Id) = query.getActivity(id = id.value)
        .executeAsOneOrNull()
        ?.toActivity()

    override suspend fun insertActivity(activity: Activity) = query.insertActivity(
        id = activity.id.value,
        sport = activity.sport,
        timestamp = activity.start.toEpochMilliseconds(),
        totalTime = activity.totalTime.inWholeMilliseconds,
        totalDistance = activity.totalDistance,
        averageSpeed = activity.averageSpeed
    )

    override suspend fun getRoute(id: Activity.Id) = query.transactionWithResult {
        val routeEntity = query.getRoute(activityId = id.value)
            .executeAsOneOrNull() ?: return@transactionWithResult null
        val routeLocations = getRouteLocationPoints(routeEntity.id)
        routeEntity.toRoute(routeLocations)
    }

    override suspend fun insertRoute(route: Route) = query.insertRoute(
        id = route.id.value,
        activityId = route.activityId.value,
    )

    override suspend fun getRouteLocations(id: Route.Id) = getRouteLocationPoints(id.value)

    override suspend fun insertLocation(location: Location, routeId: Route.Id) {
        query.insertLocation(
            id = null,
            routeId = routeId.value,
            latitude = location.latitude,
            longitute = location.longitude,
            timestamp = location.timestamp.toEpochMilliseconds(),
        )
    }

    private fun getRouteLocationPoints(routeId: Long) = query.getRouteLocationPoints(routeId)
        .executeAsList()
        .map(LocationEntity::toLocation)
}

