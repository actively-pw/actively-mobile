package com.actively.distance

@JvmInline
value class Distance private constructor(private val rawValue: Long) {

    companion object {

        private const val METERS_IN_KILOMETERS = 1000

        private fun Number.toDistance(unit: DistanceUnit): Distance {
            val rawDistance = this.toDouble()
            require(rawDistance >= 0)
            return when (unit) {
                DistanceUnit.METERS -> Distance(rawDistance.toLong())
                DistanceUnit.KILOMETERS -> Distance((rawDistance * METERS_IN_KILOMETERS).toLong())
            }
        }

        val Int.meters: Distance get() = toDistance(DistanceUnit.METERS)

        val Long.meters: Distance get() = toDistance(DistanceUnit.METERS)

        val Int.kilometers: Distance get() = toDistance(DistanceUnit.KILOMETERS)

        val Double.kilometers: Distance get() = toDistance(DistanceUnit.KILOMETERS)

        val Distance.inMeters get() = rawValue

        val Distance.inKilometers get() = inMeters.toDouble() / METERS_IN_KILOMETERS

        val Distance.inWholeKilometers get() = (inMeters.toDouble() / METERS_IN_KILOMETERS).toInt()

        operator fun Distance.times(number: Int) = Distance(rawValue * number)

        operator fun Int.times(distance: Distance) = distance.times(this)

        operator fun Distance.times(number: Double) = Distance((rawValue * number).toLong())

        operator fun Double.times(distance: Distance) = distance.times(this)

        operator fun Distance.plus(other: Distance) = Distance(rawValue + other.rawValue)
    }
}

enum class DistanceUnit {

    METERS, KILOMETERS
}
