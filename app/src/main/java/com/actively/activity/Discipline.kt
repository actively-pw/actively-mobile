package com.actively.activity

sealed class Discipline {

    object Cycling : Discipline()
    object Running : Discipline()
    object NordicWalking : Discipline()
}

fun Discipline.asString() = when (this) {
    is Discipline.Cycling -> "cycling"
    is Discipline.Running -> "running"
    is Discipline.NordicWalking -> "nordic_walking"
}

fun String.toDiscipline() = when (this.lowercase().trim()) {
    "cycling" -> Discipline.Cycling
    "running" -> Discipline.Running
    "nordic_walking" -> Discipline.NordicWalking
    else -> error("Unknown discipline: $this.")
}
