package com.actively.location

import android.annotation.SuppressLint
import android.os.Looper
import com.actively.activity.Location
import com.actively.activity.toLocation
import com.actively.distance.Distance
import com.actively.distance.Distance.Companion.inMeters
import com.actively.distance.Distance.Companion.meters
import com.mapbox.common.location.compat.LocationEngine
import com.mapbox.common.location.compat.LocationEngineCallback
import com.mapbox.common.location.compat.LocationEngineRequest
import com.mapbox.common.location.compat.LocationEngineResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

interface LocationProvider {

    fun userLocation(
        updateInterval: Duration = 3.seconds,
        fastestUpdateInterval: Duration = 1.seconds,
        locationUpdatesDistance: Distance = 1.5.meters
    ): Flow<Location>
}

@SuppressLint("MissingPermission")
class LocationProviderImpl(private val locationEngine: LocationEngine) : LocationProvider {

    override fun userLocation(
        updateInterval: Duration,
        fastestUpdateInterval: Duration,
        locationUpdatesDistance: Distance,
    ) = callbackFlow {
        val callback = locationEngineCallback(
            onNewLocation = { trySend(it) },
            onException = { close(it) }
        )
        val locationRequest = locationEngineRequest(
            updateInterval = updateInterval,
            fastestUpdateInterval = fastestUpdateInterval,
            locationUpdatesDistance = locationUpdatesDistance
        )
        locationEngine.requestLocationUpdates(
            request = locationRequest,
            callback = callback,
            looper = Looper.getMainLooper()
        )
        awaitClose {
            locationEngine.removeLocationUpdates(callback)
        }
    }

    private fun locationEngineCallback(
        onException: (Exception) -> Unit,
        onNewLocation: (Location) -> Unit
    ) = object : LocationEngineCallback<LocationEngineResult> {
        override fun onSuccess(result: LocationEngineResult) {
            result.lastLocation?.toLocation()?.let(onNewLocation)
        }

        override fun onFailure(exception: Exception) {
            onException(exception)
            exception.printStackTrace()
        }
    }

    private fun locationEngineRequest(
        updateInterval: Duration,
        fastestUpdateInterval: Duration,
        locationUpdatesDistance: Distance,
    ) = LocationEngineRequest.Builder(updateInterval.inWholeMilliseconds)
        .setFastestInterval(fastestUpdateInterval.inWholeMilliseconds)
        .setDisplacement(locationUpdatesDistance.inMeters.toFloat())
        .build()
}
