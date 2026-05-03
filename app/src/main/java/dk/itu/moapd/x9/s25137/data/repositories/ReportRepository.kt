package dk.itu.moapd.x9.s25137.data.repositories

import androidx.core.net.toUri
import com.google.firebase.database.DatabaseError
import dk.itu.moapd.x9.s25137.data.datasources.ImageDataSource
import dk.itu.moapd.x9.s25137.data.datasources.ReportRemoteDataSource
import dk.itu.moapd.x9.s25137.domain.models.Report
import javax.inject.Inject

class ReportRepository @Inject constructor(
    private val reportRemoteDataSource: ReportRemoteDataSource,
    private val imageDataSource: ImageDataSource
) {
    fun getAllQuery() =
        reportRemoteDataSource.getAllQuery()

    fun insert(report: Report, onComplete: (DatabaseError?) -> Unit): String? {
        if (report.localImageUri == null) return reportRemoteDataSource.insert(
            report = report,
            onComplete = onComplete
        )

        var insertedReportKey: String? = null
        imageDataSource.uploadFile(
            localUri = report.localImageUri.toUri(),
            remotePath = "images/reports/${report.key}.jpg"
        ).addOnSuccessListener { downloadUri ->
            val reportWithImage = report.copy(remoteImageUri = downloadUri.toString())
            insertedReportKey = reportRemoteDataSource.insert(
                report = reportWithImage,
                onComplete = onComplete
            )
        }
        return insertedReportKey
    }

    fun update(report: Report, onComplete: (DatabaseError?) -> Unit) =
        reportRemoteDataSource.update(report = report, onComplete = onComplete)

    fun delete(key: String, onComplete: (DatabaseError?) -> Unit) =
        reportRemoteDataSource.delete(key = key, onComplete = onComplete)
}