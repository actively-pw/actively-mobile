package com.actively.util

import java.util.UUID

interface UUIDProvider {

    operator fun invoke(): String
}

class UUIDProviderImpl : UUIDProvider {

    override fun invoke() = UUID.randomUUID().toString()
}
