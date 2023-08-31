package com.actively.recorder

interface RecorderStateMachine {

    fun transitionTo(targetState: RecorderState, onSuccess: () -> Unit)
}

class RecorderStateMachineImpl : RecorderStateMachine {

    private val legalTransitions = arrayOf(
        RecorderState.Idle to RecorderState.Started,
        RecorderState.Started to RecorderState.Paused,
        RecorderState.Started to RecorderState.Stopped,
        RecorderState.Paused to RecorderState.Started,
        RecorderState.Paused to RecorderState.Stopped
    )
    private var currentState: RecorderState = RecorderState.Idle

    override fun transitionTo(targetState: RecorderState, onSuccess: () -> Unit) {
        if (currentState to targetState in legalTransitions) {
            currentState = targetState
            onSuccess()
        }
    }

}

