package com.actively.activity.usecase

import com.actively.activity.Activity
import com.actively.activity.Discipline
import com.actively.util.UUIDProvider

/**
 * Utility usecase that creates new activity.
 */
interface CreateActivityUseCase {

    operator fun invoke(sport: Discipline, id: Activity.Id? = null, title: String? = null): Activity
}

class CreateActivityUseCaseImpl(private val uuidProvider: UUIDProvider) : CreateActivityUseCase {

    override fun invoke(sport: Discipline, id: Activity.Id?, title: String?) = Activity(
        id = id ?: Activity.Id(uuidProvider()),
        title = title,
        sport = sport,
        stats = Activity.Stats.empty(),
        route = emptyList()
    )
}
