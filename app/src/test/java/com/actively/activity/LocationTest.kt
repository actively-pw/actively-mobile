package com.actively.activity

import com.actively.distance.Distance.Companion.inWholeKilometers
import com.actively.distance.Distance.Companion.kilometers
import com.actively.distance.Distance.Companion.meters
import com.actively.stubs.stubLocation
import com.mapbox.geojson.Point
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class LocationTest : FunSpec({

    val newYork = stubLocation(latitude = 40.7128, longitude = -74.0060)
    val losAngeles = stubLocation(latitude = 34.0522, longitude = -118.2437)
    val london = stubLocation(latitude = 51.5074, longitude = -0.1278)
    val paris = stubLocation(latitude = 48.8566, longitude = 2.3522)
    val sanFrancisco = stubLocation(latitude = 37.7749, longitude = -122.4194)
    val berlin = stubLocation(latitude = 52.5200, longitude = 13.4050)

    test("distanceTo should return correct distance between two locations in large distances") {
        forAll(
            row(newYork, losAngeles, 3936.kilometers),
            row(london, paris, 343.kilometers),
            row(sanFrancisco, losAngeles, 559.kilometers),
            row(berlin, newYork, 6387.kilometers),
        ) { location1, location2, distance ->
            location1.distanceTo(location2).inWholeKilometers shouldBe distance.inWholeKilometers
        }
    }

    test("distanceTo should return correct distance between two locations in small distance") {
        forAll(
            row(
                stubLocation(latitude = 52.275118, longitude = 20.938938),
                stubLocation(latitude = 52.275064, longitude = 20.938849),
                8.meters
            ),
            row(
                stubLocation(latitude = 52.275718, longitude = 20.937945),
                stubLocation(latitude = 52.275788, longitude = 20.938326),
                27.meters
            ),
            row(
                stubLocation(latitude = 52.275718, longitude = 20.937945),
                stubLocation(latitude = 52.275236, longitude = 20.938632),
                71.meters
            ),
        ) { location1, location2, distance ->
            location1.distanceTo(location2) shouldBe distance
        }
    }

    test("toPoint should properly map Location to Point") {
        val expected = Point.fromLngLat(10.1, 55.78)
        val actual = stubLocation(longitude = 10.1, latitude = 55.78)
        actual.toPoint() shouldBe expected
    }
})
