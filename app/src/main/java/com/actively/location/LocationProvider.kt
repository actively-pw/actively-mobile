package com.actively.location

import android.annotation.SuppressLint
import android.os.Looper
import com.actively.activity.Location
import com.actively.activity.toLocation
import com.actively.distance.Distance
import com.actively.distance.Distance.Companion.inMeters
import com.mapbox.common.location.compat.LocationEngine
import com.mapbox.common.location.compat.LocationEngineCallback
import com.mapbox.common.location.compat.LocationEngineRequest
import com.mapbox.common.location.compat.LocationEngineResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.time.Duration

/**
 * Provides methods to subscribe to user location updates.
 */
interface LocationProvider {

    /**
     * Returns location updates.
     *
     * @param updateInterval - default interval of location updates,
     * @param fastestUpdateInterval - minimal interval of location updates,
     * @param displacement - minimal distance that has to be traveled to trigger next location update
     *
     * @return Flow of user location updates.
     */
    fun userLocation(
        updateInterval: Duration,
        fastestUpdateInterval: Duration,
        displacement: Distance
    ): Flow<Location>
}

@SuppressLint("MissingPermission")
class LocationProviderImpl(private val locationEngine: LocationEngine) : LocationProvider {

    override fun userLocation(
        updateInterval: Duration,
        fastestUpdateInterval: Duration,
        displacement: Distance
    ) = callbackFlow {
        val callback = locationEngineCallback(
            onNewLocation = { trySend(it) },
            onException = { close(it) }
        )
        val locationRequest = locationEngineRequest(
            updateInterval = updateInterval,
            fastestUpdateInterval = fastestUpdateInterval,
            displacement = displacement
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
        displacement: Distance
    ) = LocationEngineRequest.Builder(updateInterval.inWholeMilliseconds)
        .setFastestInterval(fastestUpdateInterval.inWholeMilliseconds)
        .setDisplacement(displacement.inMeters.toFloat())
        .build()
}
