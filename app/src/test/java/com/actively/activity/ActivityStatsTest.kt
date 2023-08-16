package com.actively.activity

import com.actively.distance.Distance.Companion.kilometers
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.hours

class ActivityStatsTest : FunSpec({

    test("empty() should create Activity.Stats with zeroed stats") {
        val expected = Activity.Stats(0.hours, 0.kilometers, 0.0)
        Activity.Stats.empty() shouldBe expected
    }
})
