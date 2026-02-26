package dk.itu.moapd.x9.s25137

import java.util.Date

data class Report(
    val title: String,
    val location: String,
    val date: Date,
    val type: Type,
    val description: String,
    val severity: Severity
) {
    override fun toString() =
        "Title: $title\n" +
                "Location: $location\n" +
                "Date: $date\n" +
                "Type: ${enumToString(type)}\n" +
                "Description: $description\n" +
                "Severity: ${enumToString(severity)}"
}

enum class Type {
    SPEED_CAMERA,
    HEAVY_TRAFFIC,
    ROAD_INCIDENTS,
    BROKEN_VEHICLES,
    OTHER;
}

enum class Severity {
    MINOR,
    MODERATE,
    MAJOR
}
