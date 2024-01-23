package com.actively.statistics.usecase

import com.actively.activity.Discipline
import com.actively.repository.StatisticsRepository
import com.actively.stubs.stubStatPage
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class GetStatisticsUseCaseTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    val repository = mockk<StatisticsRepository>(relaxed = true)
    val useCase = GetStatisticsUseCaseImpl(repository)

    test("Calls repository") {
        coEvery { repository.getAllStatistics() } returns emptyList()
        useCase()
        coVerify(exactly = 1) { repository.getAllStatistics() }
    }

    test("Wraps repository's return in Result.success if no exception was thrown") {
        val allStats = listOf(
            stubStatPage(sport = Discipline.Cycling),
            stubStatPage(sport = Discipline.Running)
        )
        coEvery { repository.getAllStatistics() } returns allStats
        useCase() shouldBe Result.success(allStats)
    }

    test("Returns Result.failure in case repository throws exception") {
        val exception = IllegalStateException()
        coEvery { repository.getAllStatistics() } throws exception
        useCase() shouldBe Result.failure(exception)
    }
})

