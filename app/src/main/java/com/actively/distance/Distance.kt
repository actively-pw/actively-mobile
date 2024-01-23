package com.actively.distance

import kotlinx.serialization.Serializable

/**
 * Class that represents distance. It removes the risk of common unit conversions. Internally it
 * holds distance in meters and converts from meters to target unit on demand.
 *
 * To create desired distance with unit use extension function on numeric types. e.g of use
 * ```
 * val distance: Distance = 2.52.kilometers // creates distance of 2.52 kilometers
 * val inMeters: Double = distance.inMeters // gets double value of meters
 * val inWholeMeters: Int = distance.inWholeMeters // gets distance value rounded to whole meters
 *
 * val dist1 = 2.5.kilometers
 * val dist2 = 2000.meters
 * val sum = dist1 + dist2 // equivalent of 4.5 km
 * sum.inMeters // 4500.0
 * sum.inKilometers // 4.5
 * ```
 *
 */
@Serializable
@JvmInline
value class Distance private constructor(private val rawValue: Double) {

    companion object {

        private const val METERS_IN_KILOMETERS = 1000

        private fun Number.toDistance(unit: DistanceUnit): Distance {
            val rawDistance = this.toDouble()
            require(rawDistance >= 0)
            return when (unit) {
                DistanceUnit.METERS -> Distance(rawDistance)
                DistanceUnit.KILOMETERS -> Distance(rawDistance * METERS_IN_KILOMETERS)
            }
        }

        val Int.meters: Distance get() = toDistance(DistanceUnit.METERS)

        val Long.meters: Distance get() = toDistance(DistanceUnit.METERS)

        val Double.meters: Distance get() = toDistance(DistanceUnit.METERS)

        val Int.kilometers: Distance get() = toDistance(DistanceUnit.KILOMETERS)

        val Double.kilometers: Distance get() = toDistance(DistanceUnit.KILOMETERS)

        val Distance.inMeters get() = rawValue

        val Distance.inWholeMeters get() = rawValue.toInt()

        val Distance.inKilometers get() = inMeters / METERS_IN_KILOMETERS

        val Distance.inWholeKilometers get() = (inMeters / METERS_IN_KILOMETERS).toInt()

        operator fun Distance.times(number: Int) = Distance(rawValue * number)

        operator fun Int.times(distance: Distance) = distance.times(this)

        operator fun Distance.times(number: Double) = Distance(rawValue * number)

        operator fun Double.times(distance: Distance) = distance.times(this)

        operator fun Distance.plus(other: Distance) = Distance(rawValue + other.rawValue)
    }
}

enum class DistanceUnit {

    METERS, KILOMETERS
}
