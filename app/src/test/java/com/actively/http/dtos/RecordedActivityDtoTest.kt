package com.actively.http.dtos

import com.actively.activity.Activity
import com.actively.activity.RecordedActivity
import com.actively.distance.Distance.Companion.kilometers
import com.actively.stubs.stubRecordedActivityDto
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.hours

class RecordedActivityDtoTest : FunSpec({

    test("Should properly map RecordedActivityDto to RecordedActivity") {
        val dto = stubRecordedActivityDto()
        val recordedActivity = RecordedActivity(
            id = RecordedActivity.Id("1"),
            title = "Morning activity",
            sport = "",
            stats = Activity.Stats(
                totalTime = 5.hours,
                averageSpeed = 20.0,
                distance = 100.kilometers
            ),
            routeUrl = "route://activity.net/1",
            mapUrl = "image://activity.net/1",
            start = Instant.fromEpochMilliseconds(0)
        )
        dto.toRecordedActivity() shouldBe recordedActivity
    }
})
