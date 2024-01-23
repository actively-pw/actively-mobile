package com.actively.datasource

data class RouteQuery(
    val id: Long,
    val start: Long,
    val timestamp: Long?,
    val latitude: Double?,
    val longitude: Double?,
    val altitude: Double?,
)
