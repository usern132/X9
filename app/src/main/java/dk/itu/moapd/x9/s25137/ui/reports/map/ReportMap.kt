package dk.itu.moapd.x9.s25137.ui.reports.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import dk.itu.moapd.x9.s25137.domain.models.Report

/* Code adapted from the MOAPD 2026 subject repository, found at https://github.com/fabricionarcizo/moapd2026/.
 * Its original license is attached below.

 * MIT License
 *
 * Copyright (c) 2026 Fabricio Batista Narcizo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

private const val MARKER_DESCRIPTION_LENGTH = 50

@Composable
fun ReportMap(
    modifier: Modifier = Modifier,
    reports: List<Report>,
    locationTrace: List<LatLng>,
    onReportInfoWindowClick: (String) -> Unit = {},
    hasLocationPermission: Boolean = false
) {
    val itu = remember { LatLng(55.6596, 12.5910) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(itu, 14f)
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            mapToolbarEnabled = false,
        ),
        properties = MapProperties(
            isTrafficEnabled = true,
            isMyLocationEnabled = hasLocationPermission
        )
    ) {
        reports.forEach { report ->
            val markerState =
                remember(report.key) {
                    MarkerState(
                        position = LatLng(
                            report.latitude,
                            report.longitude
                        )
                    )
                }
            Marker(
                state = markerState,
                title = report.title,
                snippet = if (report.description.length < MARKER_DESCRIPTION_LENGTH) report.description
                else report.description.take(MARKER_DESCRIPTION_LENGTH) + "...",
                onInfoWindowClick = { onReportInfoWindowClick(report.key!!) }
            )
        }
        Polyline(points = locationTrace, color = Color.Red)
    }
}