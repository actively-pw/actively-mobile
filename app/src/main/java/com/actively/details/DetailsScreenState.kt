package com.actively.details

sealed class DetailsScreenState {

    object Loading : DetailsScreenState()
    data class Loaded(
        val imageUrl: String,
        val details: List<DetailsRow>
    ) : DetailsScreenState()

    object Error : DetailsScreenState()
}

data class DetailsRow(
    val left: Pair<String, String>,
    val right: Pair<String, String>
)

