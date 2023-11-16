package com.actively.home.usecase

import com.actively.activity.DetailedRecordedActivity
import com.actively.activity.RecordedActivity

interface GetDetailedRecordedActivityUseCase {

    suspend operator fun invoke(id: RecordedActivity.Id): DetailedRecordedActivity
}
