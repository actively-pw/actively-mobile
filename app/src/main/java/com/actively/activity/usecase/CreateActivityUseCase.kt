package com.actively.activity.usecase

import com.actively.activity.Activity
import com.actively.util.UUIDProvider

interface CreateActivityUseCase {

    operator fun invoke(sport: String, id: Activity.Id? = null, title: String? = null): Activity
}

class CreateActivityUseCaseImpl(private val uuidProvider: UUIDProvider) : CreateActivityUseCase {

    override fun invoke(sport: String, id: Activity.Id?, title: String?) = Activity(
        id = id ?: Activity.Id(uuidProvider()),
        title = title,
        sport = sport,
        stats = Activity.Stats.empty(),
        route = emptyList()
    )
}
