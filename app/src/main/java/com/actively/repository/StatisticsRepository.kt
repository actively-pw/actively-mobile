package com.actively.repository

import com.actively.statistics.StatTab

interface StatisticsRepository {

    suspend fun getAllStatistics(): List<StatTab>
}
