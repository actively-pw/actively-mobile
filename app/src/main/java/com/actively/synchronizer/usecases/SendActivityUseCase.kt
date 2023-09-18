package com.actively.synchronizer.usecases

import com.actively.activity.Activity

interface SendActivityUseCase {

    suspend operator fun invoke(activity: Activity): Result<Unit>
}

class SendActivityUseCaseImpl(

) : SendActivityUseCase {

    override suspend fun invoke(activity: Activity): Result<Unit> {
        return Result.success(Unit)
    }
}
