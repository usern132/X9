package dk.itu.moapd.x9.s25137.ui.reports.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import dk.itu.moapd.x9.s25137.domain.models.Report

private const val MARKER_DESCRIPTION_LENGTH = 50

@Composable
fun ReportMap(
    modifier: Modifier = Modifier,
    onReportInfoWindowClick: (String) -> Unit = {},
    reports: List<Report>
) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

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
            isMyLocationEnabled = hasPermission
        )
    ) {
        reports.forEach { report ->
            val markerState =
                remember { MarkerState(position = LatLng(report.latitude, report.longitude)) }
            Marker(
                state = markerState,
                title = report.title,
                snippet = if (report.description.length < MARKER_DESCRIPTION_LENGTH) report.description
                else report.description.take(MARKER_DESCRIPTION_LENGTH) + "...",
                onInfoWindowClick = { onReportInfoWindowClick(report.key!!) }
            )
        }
    }
}