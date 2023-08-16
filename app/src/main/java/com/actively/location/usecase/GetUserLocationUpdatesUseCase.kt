package com.actively.location.usecase

import com.actively.activity.Location
import com.actively.location.LocationProvider
import kotlinx.coroutines.flow.Flow

interface GetUserLocationUpdatesUseCase {

    operator fun invoke(): Flow<Location>
}

class GetUserLocationUpdatesUseCaseImpl(
    private val locationModule: LocationProvider
) : GetUserLocationUpdatesUseCase {

    override operator fun invoke() = locationModule.userLocation()
}
