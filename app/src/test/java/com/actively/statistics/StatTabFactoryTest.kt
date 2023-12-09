package com.actively.statistics

import com.actively.stubs.stubCyclingStatTab
import com.actively.stubs.stubNordicWalkingStatTab
import com.actively.stubs.stubRunningStatTab
import com.actively.stubs.stubStatPage
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class StatTabFactoryTest : FunSpec({

    val factory = StatTabFactory()

    test("Creates correct stat tab from Cycling page") {
        factory.create(stubStatPage(sport = "cycling")) shouldBe stubCyclingStatTab()
    }

    test("Creates correct stat tab from Running page") {
        factory.create(stubStatPage(sport = "running")) shouldBe stubRunningStatTab()
    }

    test("Creates correct stat tab from Nordic Waling page") {
        factory.create(stubStatPage(sport = "nordic_walking")) shouldBe stubNordicWalkingStatTab()
    }

    test("Throws error when trying to create StatTab from unknown sport discipline") {
        shouldThrowExactly<IllegalStateException> {
            factory.create(stubStatPage(sport = "jogging"))
        }
    }
})
