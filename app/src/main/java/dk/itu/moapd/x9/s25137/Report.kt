package dk.itu.moapd.x9.s25137

import io.bloco.faker.Faker
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

    companion object {
        fun generateRandomReports(n: Int = 100): List<Report> {
            val faker = Faker()
            val trafficReports = mutableListOf<Report>()

            for (i in 1..n) {
                val trafficReport = Report(
                    title = faker.lorem.sentence(wordCount = 3),
                    location = faker.address.streetAddress(),
                    date = faker.date.backward(),
                    type = Type.entries.random(),
                    description = faker.lorem.paragraphs(1).toString(),
                    severity = Severity.entries.random()
                )
                trafficReports.add(trafficReport)
            }
            return trafficReports
        }
    }
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
