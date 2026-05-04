package dk.itu.moapd.x9.s25137.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ActionList(modifier: Modifier = Modifier, actions: Set<Action> = emptySet()) {
    if (actions.isEmpty()) return
    Column(modifier = modifier) {
        HorizontalDivider()
        actions.forEach { action ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = action.onClick)
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    action.label,
                    // (fill = false) prevents the text from pushing out the trailing composable
                    modifier = Modifier.weight(1.0f, fill = false),
                    style = MaterialTheme.typography.bodyLarge
                )
                action.trailingComposable()
            }
            HorizontalDivider()
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ActionListPreview() {
    val actions = setOf(
        Action("Example", {}),
        Action("Example 2", {}),
        Action(
            "This is a very long label that serves as a very long example with a lot of different words",
            {})
    )
    ActionList(actions = actions)
}