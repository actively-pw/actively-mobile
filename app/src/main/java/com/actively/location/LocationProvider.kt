package com.actively.location

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
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
        updateInterval: Duration,
        fastestUpdateInterval: Duration,
        locationUpdatesDistanceMeters: Float
    ): Flow<Location>

    companion object {
        val DEFAULT_UPDATE_INTERVAL = 5.seconds
        val DEFAULT_FASTEST_UPDATE_INTERVAL = 2.seconds
        const val DEFAULT_LOCATION_UPDATES_DISTANCE_METERS = 10f
    }
}

@SuppressLint("MissingPermission")
class LocationProviderImpl(private val locationEngine: LocationEngine) : LocationProvider {

    override fun userLocation(
        updateInterval: Duration,
        fastestUpdateInterval: Duration,
        locationUpdatesDistanceMeters: Float,
    ) = callbackFlow {
        val callback = onNewLocationCallback(
            onNewLocation = { trySend(it) },
            onException = { close(it) }
        )
        val locationRequest = locationEngineRequest(
            updateInterval = updateInterval,
            fastestUpdateInterval = fastestUpdateInterval,
            locationUpdatesDistanceMeters = locationUpdatesDistanceMeters
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

    private fun onNewLocationCallback(
        onException: (Exception) -> Unit,
        onNewLocation: (Location) -> Unit
    ) = object : LocationEngineCallback<LocationEngineResult> {
        override fun onSuccess(result: LocationEngineResult) {
            result.lastLocation?.let(onNewLocation)
        }

        override fun onFailure(exception: Exception) {
            onException(exception)
            exception.printStackTrace()
        }
    }

    private fun locationEngineRequest(
        updateInterval: Duration,
        fastestUpdateInterval: Duration,
        locationUpdatesDistanceMeters: Float,
    ) = LocationEngineRequest.Builder(updateInterval.inWholeMilliseconds)
        .setFastestInterval(fastestUpdateInterval.inWholeMilliseconds)
        .setDisplacement(locationUpdatesDistanceMeters)
        .build()
}
