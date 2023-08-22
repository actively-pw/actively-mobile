package com.actively.recorder

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.mockk.mockk
import io.mockk.verify

class RecorderStateMachineTest : FunSpec({

    isolationMode = IsolationMode.InstancePerTest
    val recorderStateMachine = RecorderStateMachineImpl()
    val onSuccess = mockk<Method>(relaxUnitFun = true)

    test("Should transition to Started from Idle") {
        recorderStateMachine.transitionTo(RecorderState.Started, onSuccess::invoke)
        verify(exactly = 1) { onSuccess() }
    }

    test("Should not transit to any state other that Started from idle") {
        val targetStates = listOf(RecorderState.Paused, RecorderState.Stopped)
        targetStates.forEach { state ->
            recorderStateMachine.transitionTo(state, onSuccess::invoke)
        }
        verify(exactly = 0) { onSuccess() }
    }

    test("Should transit to Paused from Started") {
        recorderStateMachine.transitionTo(RecorderState.Started) {}
        recorderStateMachine.transitionTo(RecorderState.Paused, onSuccess::invoke)
        verify(exactly = 1) { onSuccess() }
    }

    test("Should transit to Stopped from Started") {
        recorderStateMachine.transitionTo(RecorderState.Started) {}
        recorderStateMachine.transitionTo(RecorderState.Stopped, onSuccess::invoke)
        verify(exactly = 1) { onSuccess() }
    }

    test("Should not transit to Idle from Started") {
        recorderStateMachine.transitionTo(RecorderState.Started) {}
        recorderStateMachine.transitionTo(RecorderState.Idle, onSuccess::invoke)
        verify(exactly = 0) { onSuccess() }
    }

    test("Should transit to Started from Paused") {
        recorderStateMachine.transitionTo(RecorderState.Started) {}
        recorderStateMachine.transitionTo(RecorderState.Paused) {}
        recorderStateMachine.transitionTo(RecorderState.Started, onSuccess::invoke)
        verify(exactly = 1) { onSuccess() }
    }

    test("Should transit to Stopped from Paused") {
        recorderStateMachine.transitionTo(RecorderState.Started) {}
        recorderStateMachine.transitionTo(RecorderState.Paused) {}
        recorderStateMachine.transitionTo(RecorderState.Stopped, onSuccess::invoke)
        verify(exactly = 1) { onSuccess() }
    }

    test("Should not transit to Idle from Paused") {
        recorderStateMachine.transitionTo(RecorderState.Started) {}
        recorderStateMachine.transitionTo(RecorderState.Paused) {}
        recorderStateMachine.transitionTo(RecorderState.Idle, onSuccess::invoke)
        verify(exactly = 0) { onSuccess() }
    }

    test("Should not transi to any state from stopped") {
        recorderStateMachine.transitionTo(RecorderState.Started) {}
        recorderStateMachine.transitionTo(RecorderState.Paused) {}
        recorderStateMachine.transitionTo(RecorderState.Stopped) {}
        val states = listOf(RecorderState.Idle, RecorderState.Started, RecorderState.Paused)
        states.forEach { state ->
            recorderStateMachine.transitionTo(state, onSuccess::invoke)
        }
        verify(exactly = 0) { onSuccess() }
    }
})

private fun interface Method {
    operator fun invoke()
}
