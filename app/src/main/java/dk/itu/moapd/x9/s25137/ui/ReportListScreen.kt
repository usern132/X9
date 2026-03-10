package dk.itu.moapd.x9.s25137.ui

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReportListScreen() {
    Scaffold(
        contentWindowInsets = WindowInsets.systemBars,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            TrafficReportList()
        }
    }
}

@Preview
@Composable
fun ReportListScreenPreview() {
    ReportListScreen()
}