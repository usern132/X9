package dk.itu.moapd.x9.s25137.ui.main

import androidx.compose.ui.Modifier
import dk.itu.moapd.x9.s25137.domain.models.Location
import dk.itu.moapd.x9.s25137.domain.models.Report

data class MainActions(
    val onLogout: () -> Unit,
    val onInsertReport: (Report) -> Unit,
    val onEditReport: (Report) -> Unit,
    val onDeleteReport: (Report) -> Unit,
    val isReportEditable: (Report) -> Boolean,
    val isReportDeletable: (Report) -> Boolean,
    val modifier: Modifier = Modifier,
    val showLoginAlertDialog: () -> Unit,
    val showLocationRequiredAlertDialog: (String) -> Unit,
    val showLocationErrorAlertDialog: () -> Unit,
    val showNotificationRequiredAlertDialog: (String) -> Unit,
    val fetchCurrentLocation: ((Location) -> Unit, () -> Unit) -> Unit,
    val onStartLocationTracking: () -> Unit,
    val onStopLocationTracking: () -> Unit,
    val setLocationTraceEnabled: (Boolean) -> Unit,
    val showCameraRequiredAlertDialog: (String) -> Unit,
) {
    // Dummy constructor used for the Compose preview
    constructor() : this(
        onLogout = {},
        onInsertReport = {},
        onEditReport = {},
        onDeleteReport = {},
        isReportEditable = { false },
        isReportDeletable = { false },
        showLoginAlertDialog = {},
        showLocationRequiredAlertDialog = {},
        showLocationErrorAlertDialog = {},
        showNotificationRequiredAlertDialog = {},
        fetchCurrentLocation = { _, _ -> },
        onStartLocationTracking = {},
        onStopLocationTracking = {},
        setLocationTraceEnabled = {},
        showCameraRequiredAlertDialog = {}
    )
}