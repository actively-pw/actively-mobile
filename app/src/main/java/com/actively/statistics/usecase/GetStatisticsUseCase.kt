package com.actively.statistics.usecase

import com.actively.repository.StatisticsRepository
import com.actively.statistics.StatPage

interface GetStatisticsUseCase {

    suspend operator fun invoke(): Result<List<StatPage>>
}

class GetStatisticsUseCaseImpl(
    private val statisticsRepository: StatisticsRepository
) : GetStatisticsUseCase {

    override suspend fun invoke(): Result<List<StatPage>> = try {
        Result.success(statisticsRepository.getAllStatistics())
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }
}
