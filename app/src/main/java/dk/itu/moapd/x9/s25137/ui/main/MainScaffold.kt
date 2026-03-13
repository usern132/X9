package dk.itu.moapd.x9.s25137.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dk.itu.moapd.x9.s25137.R
import dk.itu.moapd.x9.s25137.ui.reports.list.ReportList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    icon = {
                        Icon(imageVector = Icons.Filled.Home, contentDescription = "Home")
                    },
                    onClick = { },
                    label = { Text(text = "Home") }
                )
            }
        },
    ) { innerPadding ->
        Box {
            ReportList { }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MainScaffoldPreview() {
    MainScaffold()
}