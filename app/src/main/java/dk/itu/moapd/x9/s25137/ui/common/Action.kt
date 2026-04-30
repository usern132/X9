package dk.itu.moapd.x9.s25137.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

data class Action(
    val label: String,
    val onClick: () -> Unit,
    val trailingComposable: @Composable () -> Unit = {
        Icon(
            Icons.AutoMirrored.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
)