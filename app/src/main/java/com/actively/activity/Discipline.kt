package com.actively.activity

sealed class Discipline {

    object Cycling : Discipline()
    object Running : Discipline()
    object NordicWalking : Discipline()
}

fun Discipline.asString() = when (this) {
    is Discipline.Cycling -> "bicycle ride"
    is Discipline.Running -> "run"
    is Discipline.NordicWalking -> "nordic walking"
}

fun String.toDiscipline() = when (this.lowercase().trim()) {
    "bicycle ride" -> Discipline.Cycling
    "run" -> Discipline.Running
    "nordic walking" -> Discipline.NordicWalking
    else -> error("Unknown discipline: $this.")
}
