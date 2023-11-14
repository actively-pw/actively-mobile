package com.actively.details

sealed class DetailsScreenState {

    object Loading : DetailsScreenState()
    data class Loaded(
        val title: String,
        val imageUrl: String,
        val typeOfActivity: String,
        val showConfirmDeleteDialog: Boolean,
        val details: List<DetailsRow>
    ) : DetailsScreenState()

    object Error : DetailsScreenState()
}

data class DetailsRow(
    val left: Pair<String, String>,
    val right: Pair<String, String>
)

