package com.actively.synchronizer

/**
 * Represents possible states of
 */
sealed class WorkState {

    object Enqueued : WorkState()
    object Running : WorkState()
    object NoInternetConnection : WorkState()
}
