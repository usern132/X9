package dk.itu.moapd.x9.s25137.ui.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dk.itu.moapd.x9.s25137.R
import dk.itu.moapd.x9.s25137.ui.common.ActionList

@Composable
fun BaseAccountScreen(
    modifier: Modifier = Modifier,
    actionListActions: Map<Int, () -> Unit> = emptyMap(),
    logInOrLogOutButton: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        content()
        Box(modifier = Modifier.padding(16.dp)) {
            logInOrLogOutButton()
        }
        ActionList(modifier = Modifier.padding(vertical = 16.dp), actions = actionListActions)
    }
}

@Composable
@Preview(showBackground = true)
fun BaseAccountScreenPreview() {
    BaseAccountScreen(
        actionListActions = mapOf(
            R.string.settings to {},
        ),
        logInOrLogOutButton = {
            Button(onClick = {}) { Text(stringResource(R.string.log_in)) }
        }) {
        Text(stringResource(R.string.log_in_message))
    }
}