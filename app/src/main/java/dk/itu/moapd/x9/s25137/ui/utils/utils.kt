package dk.itu.moapd.x9.s25137.ui.utils

import android.util.Patterns
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import java.util.regex.Pattern

private const val MIN_PASSWORD_LENGTH = 8
private const val PASSWORD_PATTERN =
    "^(?=.{$MIN_PASSWORD_LENGTH,}\$)(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).*\$\n"

@Composable
fun PlaceholderScreen(name: String = "Placeholder") {
    val name = name.replaceFirstChar { it.uppercase() }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("$name screen - Work in progress")
    }
}

fun String.isEmailValid(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isPasswordValid(): Boolean {
    return Pattern.compile(PASSWORD_PATTERN).matcher(this).matches()
}