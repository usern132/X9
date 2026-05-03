package dk.itu.moapd.x9.s25137.domain.models

import android.text.format.DateFormat
import androidx.annotation.StringRes
import com.google.firebase.database.Exclude
import dk.itu.moapd.x9.s25137.R
import io.bloco.faker.Faker
import java.util.Date
import kotlin.random.Random

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
    @get:Exclude
    val key: String? = null,
    val title: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String = "",
    val timestamp: Long = 0L,
    val type: Type = Type.OTHER,
    val description: String = "",
    val severity: Severity = Severity.MINOR,

    // The local image URI gets excluded because it is the location of an image that was
    // just attached/captured in the local filesystem as a temporary file.
    @get:Exclude
    val localImageUri: String? = null,
    // The remote image URI will be assigned once it is uploaded to the storage bucket.
    val remoteImageUri: String? = null,

    val userId: String = "",
    val userName: String = "",
    val userImageUri: String? = null
) {
    companion object {
        val previewReports = listOf(
            Report(
                key = "1",
                title = "Traffic jam on E20",
                latitude = 55.6761,
                longitude = 12.5683,
                address = "E20, Copenhagen",
                timestamp = 1777636800000L, // May 1, 2026, 12:00 PM UTC
                type = Type.HEAVY_TRAFFIC,
                description = "Major congestion due to an accident.",
                severity = Severity.MAJOR,
                userId = "user1",
                userName = "Alice Smith"
            ),
            Report(
                key = "2",
                title = "Broken car in lane 1",
                latitude = 55.6600,
                longitude = 12.5900,
                address = "Amagerbrogade 12",
                timestamp = 1777896000000L, // May 4, 2026
                type = Type.BROKEN_VEHICLES,
                description = "Vehicle broken down blocking one lane.",
                severity = Severity.MODERATE,
                userId = "user2",
                userName = "Bob Jones"
            ),
            Report(
                key = "3",
                title = "Speed camera ahead",
                latitude = 55.7000,
                longitude = 12.5000,
                address = "Lyngbyvej",
                timestamp = 1778155200000L, // May 7, 2026
                type = Type.SPEED_CAMERA,
                description = "Mobile speed camera spotted.",
                severity = Severity.MINOR,
                userId = "user3",
                userName = "Charlie Brown"
            ),
            Report(
                key = "4",
                title = "Road construction",
                latitude = 55.6800,
                longitude = 12.5800,
                address = "Nørrebrogade",
                timestamp = 1778414400000L, // May 10, 2026
                type = Type.ROAD_INCIDENTS,
                description = "Road works causing delays.",
                severity = Severity.MODERATE,
                userId = "user4",
                userName = "Diana Prince"
            ),
            Report(
                key = "5",
                title = "Icy road conditions",
                latitude = 55.7200,
                longitude = 12.4500,
                address = "Hillerødmotorvejen",
                timestamp = 1778673600000L, // May 13, 2026
                type = Type.OTHER,
                description = "Very slippery surface reported.",
                severity = Severity.MAJOR,
                userId = "user5",
                userName = "Ethan Hunt"
            ),
            Report(
                key = "6",
                title = "Minor fender bender",
                latitude = 55.6500,
                longitude = 12.6000,
                address = "Kastrupvej",
                timestamp = 1778932800000L, // May 16, 2026
                type = Type.ROAD_INCIDENTS,
                description = "Two cars collided, no injuries.",
                severity = Severity.MINOR,
                userId = "user6",
                userName = "Fiona Gallagher"
            ),
            Report(
                key = "7",
                title = "Stalled truck",
                latitude = 55.6900,
                longitude = 12.5500,
                address = "Jagtvej",
                timestamp = 1779192000000L, // May 19, 2026
                type = Type.BROKEN_VEHICLES,
                description = "Large truck stalled in the middle of the intersection.",
                severity = Severity.MAJOR,
                userId = "user7",
                userName = "George Miller"
            ),
            Report(
                key = "8",
                title = "Police checkpoint",
                latitude = 55.6700,
                longitude = 12.5700,
                address = "Vesterbrogade",
                timestamp = 1779451200000L, // May 22, 2026
                type = Type.OTHER,
                description = "Routine police checks.",
                severity = Severity.MINOR,
                userId = "user8",
                userName = "Hannah Abbott"
            ),
            Report(
                key = "9",
                title = "Slow moving vehicle",
                latitude = 55.7100,
                longitude = 12.4800,
                address = "Ring 2",
                timestamp = 1779710400000L, // May 25, 2026
                type = Type.OTHER,
                description = "Tractor moving very slowly.",
                severity = Severity.MINOR,
                userId = "user9",
                userName = "Ian Wright"
            ),
            Report(
                key = "10",
                title = "Heavy rain - low visibility",
                latitude = 55.6400,
                longitude = 12.6200,
                address = "Øresundsmotorvejen",
                timestamp = 1779969600000L, // May 28, 2026
                type = Type.OTHER,
                description = "Visibility is very low due to downpour.",
                severity = Severity.MODERATE,
                userId = "user10",
                userName = "Jane Doe"
            )
        )

        fun generateRandomReports(n: Int = 20): List<Report> {
            val faker = Faker()
            val reports = mutableListOf<Report>()

            repeat(n) {
                val report = Report(
                    key = faker.number.hexadecimal(digits = 8),
                    title = faker.lorem.sentence(wordCount = 3),
                    latitude = Random.nextDouble(0.0, 50.0),
                    longitude = Random.nextDouble(0.0, 50.0),
                    address = faker.address.streetAddress(),
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
