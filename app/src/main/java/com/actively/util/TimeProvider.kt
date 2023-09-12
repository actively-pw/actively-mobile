package com.actively.util

import kotlinx.datetime.Instant

fun interface TimeProvider : () -> Instant
