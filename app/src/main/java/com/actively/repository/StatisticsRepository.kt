package com.actively.repository

import com.actively.statistics.StatPage

interface StatisticsRepository {

    suspend fun getAllStatistics(): List<StatPage>
}
