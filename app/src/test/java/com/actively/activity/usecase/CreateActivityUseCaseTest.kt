package com.actively.activity.usecase

import com.actively.activity.Activity
import com.actively.stubs.stubActivity
import com.actively.util.UUIDProvider
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.Instant

class CreateActivityUseCaseTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    val uuidProvider = mockk<UUIDProvider>()
    val createActivityUseCase = CreateActivityUseCaseImpl(uuidProvider)

    beforeTest {
        every { uuidProvider.invoke() } returns "uuid-123"
    }

    test("Creates proper activity from given parameters") {
        val startTimestamp = Instant.fromEpochMilliseconds(0)
        forAll(
            row(
                "Cycling",
                startTimestamp,
                "Morning activity",
                Activity.Id("1"),
                stubActivity(stats = Activity.Stats.empty(), route = emptyList())
            ),
            row(
                "Cycling",
                startTimestamp,
                "Morning activity",
                null,
                stubActivity(id = "uuid-123", stats = Activity.Stats.empty(), route = emptyList())
            ),
            row(
                "Cycling",
                startTimestamp,
                null,
                null,
                stubActivity(
                    id = "uuid-123",
                    title = null,
                    stats = Activity.Stats.empty(),
                    route = emptyList()
                )
            ),
        ) { sport, start, title, id, expected ->
            createActivityUseCase(sport, start, id, title) shouldBe expected
        }
    }

    test("Should not call UUIDProvider if activity id was passed in parameter") {
        createActivityUseCase(
            id = Activity.Id("1"),
            sport = "Cycling",
            start = Instant.fromEpochMilliseconds(0)
        )
        verify(exactly = 0) { uuidProvider.invoke() }
    }

    test("Should call UUIDProvider if no activity id was passed in parameter") {
        createActivityUseCase(sport = "Cycling", start = Instant.fromEpochMilliseconds(0))
        verify(exactly = 1) { uuidProvider.invoke() }
    }
})
