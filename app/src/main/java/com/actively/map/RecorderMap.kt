package com.actively.map

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.actively.ui.theme.DarkColors
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.locationcomponent.location

private const val SOURCE_ID = "source-id"
private const val LINE_LAYER_ID = "line-layer-id"

@Composable
fun RecorderMap(
    routeGeoJson: String?,
    modifier: Modifier = Modifier,
) {
    val lineColor = DarkColors.tertiaryContainer
    Column(modifier = modifier) {
        AndroidView(
            factory = { context ->
                MapView(context, mapInitOptions = mapInitOptions(context)).apply {
                    layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    getMapboxMap().apply {
                        loadStyle(
                            style(Style.OUTDOORS) {
                                +geoJsonSource(SOURCE_ID)
                                +lineLayer(LINE_LAYER_ID, SOURCE_ID) {
                                    lineWidth(4.0)
                                    lineColor(lineColor.toArgb())
                                }
                            }
                        )
                    }
                    location.updateSettings {
                        enabled = true
                    }
                }
            },
            update = { mapView ->
                routeGeoJson?.let { geoJson ->
                    mapView.getMapboxMap().getStyle()
                        ?.getSourceAs<GeoJsonSource>(SOURCE_ID)
                        ?.data(geoJson)
                }
            }
        )
    }
}
