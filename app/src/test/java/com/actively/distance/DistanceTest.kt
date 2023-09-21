package com.actively.distance

import com.actively.distance.Distance.Companion.inKilometers
import com.actively.distance.Distance.Companion.inMeters
import com.actively.distance.Distance.Companion.inWholeKilometers
import com.actively.distance.Distance.Companion.kilometers
import com.actively.distance.Distance.Companion.meters
import com.actively.distance.Distance.Companion.plus
import com.actively.distance.Distance.Companion.times
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class DistanceTest : FunSpec({

    context("conversions") {
        test("Should convert given Int number to distance properly") {
            forAll(
                row(5000.meters, 5000.0, 5.0),
                row(2500.meters, 2500.0, 2.5),
                row(250.meters, 250.0, 0.250),
                row(0.meters, 0.0, 0.0),
                row(6.kilometers, 6000.0, 6.0),
                row(1.kilometers, 1000.0, 1.0),
                row(0.kilometers, 0.0, 0.0),
            ) { distance, inMeters, inKilometers ->
                distance.inMeters shouldBe inMeters
                distance.inKilometers shouldBe inKilometers
            }
        }

        test("Should convert given Double number to distance properly") {
            forAll(
                row(6.52.kilometers, 6520.0, 6.52),
                row(1.66.kilometers, 1660.0, 1.66),
                row(0.0.kilometers, 0.0, 0),
            ) { distance, inMeters, inKilometers ->
                distance.inMeters shouldBe inMeters
                distance.inKilometers shouldBe inKilometers
            }
        }

        test("The same distances should be equal") {
            forAll(
                row(1000.meters, 1.kilometers),
                row(505.meters, 0.505.kilometers),
                row(500.meters, 0.5.kilometers),
                row(0.meters, 0.kilometers),
            ) { dist1, dist2 ->
                dist1 shouldBe dist2
            }
        }

        test("in* should return exact value of meters/kilometers") {
            forAll(
                row(1000.meters, 1000, 1.0),
                row(505.meters, 505, 0.505),
                row(500.meters, 500, 0.5),
                row(0.meters, 0, 0.0),
            ) { distance, expectedMeters, expectedKilometers ->
                distance.inMeters shouldBe expectedMeters
                distance.inKilometers shouldBe expectedKilometers
            }
        }

        test("inWholeKilometers should return whole value of kilometers") {
            forAll(
                row(1000.meters, 1),
                row(1.5.kilometers, 1),
                row(1.9999.kilometers, 1),
                row(500.meters, 0),
                row(0.meters, 0),
            ) { distance, expectedKilometers ->
                distance.inWholeKilometers shouldBe expectedKilometers
            }
        }
    }

    context("arithmetic operators") {

        test("times operator returns correct distance") {
            forAll(
                row(10.meters, 10, 100.meters),
                row(10.kilometers, 10, 100.kilometers),
                row(10.5.kilometers, 10, 105.kilometers),
                row(0.meters, 100, 0.meters),
                row(0.kilometers, 100, 0.kilometers)
            ) { distance, number, result ->
                distance * number shouldBe result
                number * distance shouldBe result
            }
        }

        test("plus operator returns correct distance") {
            forAll(
                row(10.meters, 20.meters, 30.meters),
                row(10.kilometers, 20.kilometers, 30.kilometers),
                row(10.5.kilometers, 20.5.kilometers, 31.kilometers),
                row(0.meters, 10.meters, 10.meters),
                row(0.kilometers, 10.kilometers, 10.kilometers)
            ) { distance1, distance2, result ->
                distance1 + distance2 shouldBe result
                distance2 + distance1 shouldBe result
            }
        }

        test("throws IllegalArgumentException when converting negative number to meters") {
            shouldThrow<IllegalArgumentException> { (-1).meters }
        }

        test("throws IllegalArgumentException when converting negative number to kilometers") {
            shouldThrow<IllegalArgumentException> { (-1).kilometers }
        }
    }
})
