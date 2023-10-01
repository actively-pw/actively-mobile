package com.actively.synchronizer

sealed class WorkState {

    object Enqueued : WorkState()
    object Running : WorkState()
    object NoInternetConnection : WorkState()
}
