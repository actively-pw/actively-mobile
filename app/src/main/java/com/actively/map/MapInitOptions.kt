package com.actively.map

import android.content.Context
import com.actively.BuildConfig
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.ResourceOptions

fun mapInitOptions(context: Context): MapInitOptions {
    val options = ResourceOptions.Builder()
        .accessToken(BuildConfig.MAPBOX_PRIVATE_TOKEN)
        .build()
    return MapInitOptions(context, resourceOptions = options)
}
