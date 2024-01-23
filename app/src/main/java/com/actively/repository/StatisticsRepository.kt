package com.actively.repository

import com.actively.http.client.AuthorizedKtorClient
import com.actively.http.dtos.StatisticsSummaryDto
import com.actively.statistics.StatPage
import io.ktor.client.call.body
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType

/**
 * Methods for data manipulation associated with summary statistics
 */
interface StatisticsRepository {

    suspend fun getAllStatistics(): List<StatPage>
}

class StatisticsRepositoryImpl(
    private val client: AuthorizedKtorClient
) : StatisticsRepository {

    override suspend fun getAllStatistics(): List<StatPage> {
        val response = client.request("/Users/summaryStatistics") {
            method = HttpMethod.Get
            contentType(ContentType.Application.Json)
        }
        return response.body<StatisticsSummaryDto>().toStatPageList()
    }
}
