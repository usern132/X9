package dk.itu.moapd.x9.s25137.ui.reports.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dk.itu.moapd.x9.s25137.domain.models.Report

@Composable
fun ReportDetailsPage(
    report: Report,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = report.title)
            Text(text = report.location)
        }
    }
}