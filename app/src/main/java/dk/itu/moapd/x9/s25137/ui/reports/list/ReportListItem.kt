package dk.itu.moapd.x9.s25137.ui.reports.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dk.itu.moapd.x9.s25137.R
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.domain.models.Severity
import dk.itu.moapd.x9.s25137.domain.models.Type
import dk.itu.moapd.x9.s25137.domain.models.toFormattedString
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun ReportListItem(
    report: Report,
    isDeletable: Boolean,
    onDelete: (report: Report) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeletionConfirmationDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState()

    LaunchedEffect(swipeToDismissBoxState.currentValue) {
        if (swipeToDismissBoxState.currentValue != SwipeToDismissBoxValue.Settled) {
            showDeletionConfirmationDialog = true
        }
    }

    if (showDeletionConfirmationDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeletionConfirmationDialog = false
                scope.launch { swipeToDismissBoxState.reset() }
            },
            title = { Text(text = stringResource(R.string.delete_report)) },
            text = { Text(text = stringResource(R.string.delete_report_confirmation)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeletionConfirmationDialog = false
                    onDelete(report)
                }) {
                    Text(text = stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeletionConfirmationDialog = false
                    scope.launch { swipeToDismissBoxState.reset() }
                }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

    if (isDeletable) {
        val backgroundIconAlignment =
            if (swipeToDismissBoxState.dismissDirection == SwipeToDismissBoxValue.StartToEnd)
                Alignment.CenterStart
            else Alignment.CenterEnd
        SwipeToDismissBox(
            state = swipeToDismissBoxState,
            backgroundContent = {
                if (swipeToDismissBoxState.dismissDirection != SwipeToDismissBoxValue.Settled)
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.delete),
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Red)
                            .wrapContentSize(backgroundIconAlignment)
                            .padding(12.dp),
                        tint = Color.White
                    )
            },
            content = { ReportListItemContent(modifier, report) }
        )
    } else {
        ReportListItemContent(modifier, report)
    }
}

@Composable
private fun ReportListItemContent(
    modifier: Modifier,
    report: Report
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp)
    ) {
        Text(
            text = report.title,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = Date(report.timestamp).toFormattedString(includeTime = true),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReportListItemPreview() {
    val report = Report(
        title = "Broken car in road",
        timestamp = Date().time,
        type = Type.BROKEN_VEHICLES,
        description = "A broken car is parked in the road",
        severity = Severity.MODERATE
    )
    ReportListItem(report = report, onDelete = {}, isDeletable = false)
}