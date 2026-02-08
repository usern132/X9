import java.util.Date

data class Report(
    val title: String,
    val location: String,
    val date: Date,
    val type: Type,
    val description: String,
    val severity: Severity
)

enum class Type {
    SPEED_CAMERA,
    HEAVY_TRAFFIC,
    ROAD_INCIDENTS,
    BROKEN_VEHICLES,
    OTHER
}

enum class Severity {
    MINOR,
    MODERATE,
    MAJOR
}
