package com.actively.map

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.actively.BuildConfig
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.ResourceOptions
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.locationcomponent.location

@Composable
fun RecorderMap(
    routeGeoJson: String?,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = isSystemInDarkTheme()
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            MapView(context, mapInitOptions = mapInitOptions(context)).apply {
                getMapboxMap().loadStyle(
                    style(if (isDarkTheme) Style.DARK else Style.OUTDOORS) {
                        +geoJsonSource("source-id")
                        +lineLayer("line-layer", "source-id") {
                            lineWidth(4.0)
                            lineColor(Color.Blue.toArgb())
                        }
                    }
                )
                location.updateSettings {
                    enabled = true
                }
            }
        },
        update = { mapView ->
            routeGeoJson?.let { geoJson ->
                mapView.getMapboxMap().getStyle()
                    ?.getSourceAs<GeoJsonSource>("source-id")
                    ?.data(geoJson)
            }
        }
    )
}

private fun mapInitOptions(context: Context): MapInitOptions {
    val options = ResourceOptions.Builder()
        .accessToken(BuildConfig.MAPBOX_PRIVATE_TOKEN)
        .build()
    return MapInitOptions(context, resourceOptions = options)
}
