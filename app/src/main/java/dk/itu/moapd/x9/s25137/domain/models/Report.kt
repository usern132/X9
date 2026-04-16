package dk.itu.moapd.x9.s25137.domain.models

import android.text.format.DateFormat
import androidx.annotation.StringRes
import com.google.firebase.database.Exclude
import dk.itu.moapd.x9.s25137.R
import io.bloco.faker.Faker
import java.util.Date

fun Date.toFormattedString(includeTime: Boolean = false, timeSeparator: String = "·"): String {
    var result: String = DateFormat.format("dd/MM/yyyy", this).toString()
    if (includeTime) result += " $timeSeparator ${DateFormat.format("HH:mm", this)}"
    return result
}

data class Report(
    /* The "key" attribute gets excluded when serialising a Report to save it to Firebase Realtime Database.
     * After inserting the Report, we can obtain the key created by Firebase
     * and make a copy of the object with that key assigned to it. We do NOT have control over the key creation.
     */
    @get:Exclude val key: String? = null,
    val title: String = "",
    val location: String = "",
    val timestamp: Long = 0L,
    val type: Type = Type.OTHER,
    val description: String = "",
    val severity: Severity = Severity.MINOR,
    val userId: String = "",
    val userName: String = "",
    val userImageUri: String? = null
) {
    override fun toString() =
        "Title: $title\n" +
                "Location: $location\n" +
                "Date: ${Date(timestamp).toFormattedString(includeTime = true)}\n" +
                "Type: ${type.name}\n" +
                "Description: $description\n" +
                "Severity: ${severity.name}" +
                "User: $userId" +
                "User Name: $userName" +
                "User Image Uri: $userImageUri"

    companion object {
        fun generateRandomReports(n: Int = 100): List<Report> {
            val faker = Faker()
            val reports = mutableListOf<Report>()

            repeat(n) {
                val report = Report(
                    key = faker.number.hexadecimal(digits = 8),
                    title = faker.lorem.sentence(wordCount = 3),
                    location = faker.address.streetAddress(),
                    timestamp = faker.date.backward().time,
                    type = Type.entries.random(),
                    description = faker.lorem.paragraphs(1).toString(),
                    severity = Severity.entries.random(),
                    userId = faker.number.hexadecimal(digits = 8),
                    userName = faker.name.name(),
                    userImageUri = null
                )
                reports.add(report)
            }
            return reports
        }
    }
}

enum class Type(
    @param:StringRes val nameResId: Int
) {
    SPEED_CAMERA(R.string.speed_camera),
    HEAVY_TRAFFIC(R.string.heavy_traffic),
    ROAD_INCIDENTS(R.string.road_incidents),
    BROKEN_VEHICLES(R.string.broken_vehicles),
    OTHER(R.string.other)
}

enum class Severity(
    @param:StringRes val nameResId: Int
) {
    MINOR(R.string.minor),
    MODERATE(R.string.moderate),
    MAJOR(R.string.major)
}
