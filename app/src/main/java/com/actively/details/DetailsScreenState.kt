package com.actively.details

import com.actively.activity.Discipline
import com.actively.home.ui.RecordedActivityTime

sealed class DetailsScreenState {

    object Loading : DetailsScreenState()
    data class Loaded(
        val title: String,
        val imageUrl: String,
        val sport: Discipline,
        val time: RecordedActivityTime,
        val showConfirmDeleteDialog: Boolean,
        val details: List<DetailsRow>
    ) : DetailsScreenState()

    object Error : DetailsScreenState()
}

data class DetailsRow(
    val left: Pair<Int, String>,
    val right: Pair<Int, String>
)
