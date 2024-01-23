package com.actively.http.dtos

import com.actively.activity.DynamicMapData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DynamicMapDataDto(
    @SerialName("routeUrl") val geoJsonUrl: String
) {

    fun toDynamicMapData() = DynamicMapData(geoJsonUrl)
}
