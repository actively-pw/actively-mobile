package com.actively.statistics.usecase

import com.actively.repository.StatisticsRepository
import com.actively.statistics.StatTab

interface GetStatisticsUseCase {

    suspend operator fun invoke(): Result<List<StatTab>>
}

class GetStatisticsUseCaseImpl(
    private val statisticsRepository: StatisticsRepository
) : GetStatisticsUseCase {

    override suspend fun invoke(): Result<List<StatTab>> = try {
        Result.success(statisticsRepository.getAllStatistics())
    } catch (e: Exception) {
        Result.failure(e)
    }
}
