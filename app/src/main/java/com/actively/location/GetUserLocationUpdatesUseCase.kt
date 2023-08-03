package com.actively.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface GetUserLocationUpdatesUseCase {

    operator fun invoke(): Flow<Location>
}

class GetUserLocationUpdatesUseCaseImpl(
    private val locationModule: LocationProvider
) : GetUserLocationUpdatesUseCase {

    override operator fun invoke() = locationModule.userLocation(
        updateInterval = LocationProvider.DEFAULT_UPDATE_INTERVAL,
        fastestUpdateInterval = LocationProvider.DEFAULT_FASTEST_UPDATE_INTERVAL,
        locationUpdatesDistanceMeters = LocationProvider.DEFAULT_LOCATION_UPDATES_DISTANCE_METERS,
    )
}
