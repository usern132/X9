package dk.itu.moapd.x9.s25137.domain.models

import android.text.format.DateFormat
import androidx.annotation.StringRes
import dk.itu.moapd.x9.s25137.R
import io.bloco.faker.Faker
import java.util.Date

fun Date.toFormattedString(includeTime: Boolean = false): String {
    var result: String = DateFormat.format("dd/MM/yyyy", this).toString()
    if (includeTime) result += " - ${DateFormat.format("HH:mm", this)}"
    return result
}

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
                "Date: ${date.toFormattedString()}\n" +
                "Type: ${type.name}\n" +
                "Description: $description\n" +
                "Severity: ${severity.name}"

    companion object {
        fun generateRandomReports(n: Int = 100): List<Report> {
            val faker = Faker()
            val reports = mutableListOf<Report>()

            for (i in 1..n) {
                val report = Report(
                    title = faker.lorem.sentence(wordCount = 3),
                    location = faker.address.streetAddress(),
                    date = faker.date.backward(),
                    type = Type.entries.random(),
                    description = faker.lorem.paragraphs(1).toString(),
                    severity = Severity.entries.random()
                )
                reports.add(report)
            }
            return reports
        }
    }
}

enum class Type(
    @StringRes val nameResId: Int
) {
    SPEED_CAMERA(R.string.speed_camera),
    HEAVY_TRAFFIC(R.string.heavy_traffic),
    ROAD_INCIDENTS(R.string.road_incidents),
    BROKEN_VEHICLES(R.string.broken_vehicles),
    OTHER(R.string.other)
}

enum class Severity(
    @StringRes val nameResId: Int
) {
    MINOR(R.string.minor),
    MODERATE(R.string.moderate),
    MAJOR(R.string.major)
}
