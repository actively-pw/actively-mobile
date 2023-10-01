package com.actively.synchronizer.usecases

import androidx.work.WorkInfo
import androidx.work.WorkManager
import app.cash.turbine.test
import com.actively.synchronizer.SynchronizeActivitiesWorker
import com.actively.synchronizer.WorkState
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

class GetSyncStateUseCaseTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    coroutineTestScope = true
    val workManager = mockk<WorkManager>()
    val useCase = GetSyncStateUseCaseImpl(workManager)

    test("Correctly maps WorkInfo.State to WorkState") {
        every { workManager.getWorkInfosForUniqueWorkFlow(SynchronizeActivitiesWorker.SYNC_WORK_NAME) } returns flowOf(
            listOf(
                WorkInfo(
                    id = UUID.randomUUID(),
                    state = WorkInfo.State.ENQUEUED,
                    tags = emptySet()
                )
            ),
            listOf(
                WorkInfo(
                    id = UUID.randomUUID(),
                    state = WorkInfo.State.RUNNING,
                    tags = emptySet()
                )
            ),
            listOf(
                WorkInfo(
                    id = UUID.randomUUID(),
                    state = WorkInfo.State.SUCCEEDED,
                    tags = emptySet()
                ),
            )
        )
        useCase().test {
            awaitItem() shouldBe WorkState.Enqueued
            awaitItem() shouldBe WorkState.Running
            awaitItem().shouldBeNull()
            awaitComplete()
        }
    }

    test("Correctly maps to WorkState when no internet connection available") {
        every { workManager.getWorkInfosForUniqueWorkFlow(SynchronizeActivitiesWorker.SYNC_WORK_NAME) } returns flowOf(
            listOf(
                WorkInfo(
                    UUID.randomUUID(),
                    state = WorkInfo.State.RUNNING,
                    tags = emptySet(),
                    stopReason = WorkInfo.STOP_REASON_CONSTRAINT_CONNECTIVITY
                )
            )
        )
        useCase().test {
            awaitItem() shouldBe WorkState.NoInternetConnection
            awaitComplete()
        }
    }

    test("Maps to null if no work available") {
        every { workManager.getWorkInfosForUniqueWorkFlow(SynchronizeActivitiesWorker.SYNC_WORK_NAME) } returns flowOf(
            emptyList()
        )
        useCase().test {
            awaitItem().shouldBeNull()
            awaitComplete()
        }
    }

    test("Maps to null if unhandled state is present") {
        every { workManager.getWorkInfosForUniqueWorkFlow(SynchronizeActivitiesWorker.SYNC_WORK_NAME) } returns flowOf(
            listOf(
                WorkInfo(
                    id = UUID.randomUUID(),
                    state = WorkInfo.State.CANCELLED,
                    tags = emptySet()
                )
            ),
            listOf(
                WorkInfo(
                    id = UUID.randomUUID(),
                    state = WorkInfo.State.SUCCEEDED,
                    tags = emptySet()
                )
            ),
            listOf(
                WorkInfo(
                    id = UUID.randomUUID(),
                    state = WorkInfo.State.BLOCKED,
                    tags = emptySet()
                ),
            ),
            listOf(
                WorkInfo(
                    id = UUID.randomUUID(),
                    state = WorkInfo.State.FAILED,
                    tags = emptySet()
                ),
            )
        )
        useCase().test {
            awaitItem().shouldBeNull()
            awaitItem().shouldBeNull()
            awaitItem().shouldBeNull()
            awaitItem().shouldBeNull()
            awaitComplete()
        }
    }
})
