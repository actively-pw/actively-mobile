package com.actively.location

import io.kotest.core.spec.style.FunSpec
import io.mockk.mockk
import io.mockk.verify

class GetUserLocationUpdatesUseCaseTest : FunSpec({

    val locationProvider = mockk<LocationProvider>(relaxed = true)
    val useCase = GetUserLocationUpdatesUseCaseImpl(locationProvider)

    test("Should call userLocation with default parameters supplied by LocationProvider") {
        useCase.invoke()
        verify {
            locationProvider.userLocation(
                LocationProvider.DEFAULT_UPDATE_INTERVAL,
                LocationProvider.DEFAULT_FASTEST_UPDATE_INTERVAL,
                LocationProvider.DEFAULT_LOCATION_UPDATES_DISTANCE_METERS
            )
        }
    }
})
