package dk.itu.moapd.x9.s25137.common

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

object Utils {
    fun timestampToLocalDate(timestamp: Long): LocalDate =
        Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault()).toLocalDate()
}