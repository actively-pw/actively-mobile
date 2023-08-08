package com.actively.location.usecase

import android.location.Location
import com.actively.location.LocationProvider
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
