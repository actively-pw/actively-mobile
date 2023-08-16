package com.actively.location.usecase

import com.actively.location.LocationProvider
import io.kotest.core.spec.style.FunSpec
import io.mockk.mockk
import io.mockk.verify

class GetUserLocationUpdatesUseCaseTest : FunSpec({

    val locationProvider = mockk<LocationProvider>(relaxed = true)
    val useCase = GetUserLocationUpdatesUseCaseImpl(locationProvider)

    test("Should call userLocation with default parameters supplied by LocationProvider") {
        useCase.invoke()
        verify { locationProvider.userLocation() }
    }
})
