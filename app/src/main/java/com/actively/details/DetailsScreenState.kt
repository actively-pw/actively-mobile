package com.actively.details

data class DetailsScreenState(
    val imageUrl: String,
    val details: List<DetailsRow>
)

data class DetailsRow(
    val left: Pair<String, String>,
    val right: Pair<String, String>
)

