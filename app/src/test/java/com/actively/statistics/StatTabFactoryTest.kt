package com.actively.statistics

import com.actively.activity.Discipline
import com.actively.stubs.stubCyclingStatTab
import com.actively.stubs.stubNordicWalkingStatTab
import com.actively.stubs.stubRunningStatTab
import com.actively.stubs.stubStatPage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class StatTabFactoryTest : FunSpec({

    val factory = StatTabFactory()

    test("Creates correct stat tab from Cycling page") {
        factory.create(stubStatPage(sport = Discipline.Cycling)) shouldBe stubCyclingStatTab()
    }

    test("Creates correct stat tab from Running page") {
        factory.create(stubStatPage(sport = Discipline.Running)) shouldBe stubRunningStatTab()
    }

    test("Creates correct stat tab from Nordic Waling page") {
        factory.create(stubStatPage(sport = Discipline.NordicWalking)) shouldBe stubNordicWalkingStatTab()
    }
})
