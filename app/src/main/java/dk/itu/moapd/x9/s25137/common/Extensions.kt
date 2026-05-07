package dk.itu.moapd.x9.s25137.common

import android.text.format.DateFormat
import java.util.Date

fun Date.toFormattedString(includeTime: Boolean = false, timeSeparator: String = "·"): String {
    var result: String = DateFormat.format("dd/MM/yyyy", this).toString()
    if (includeTime) result += " $timeSeparator ${DateFormat.format("HH:mm", this)}"
    return result
}