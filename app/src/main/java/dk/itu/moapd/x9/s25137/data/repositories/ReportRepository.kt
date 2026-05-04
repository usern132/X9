package dk.itu.moapd.x9.s25137.data.repositories

import android.net.Uri
import androidx.core.net.toUri
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseError
import dk.itu.moapd.x9.s25137.data.datasources.DatabaseRemoteDataSource
import dk.itu.moapd.x9.s25137.data.datasources.ImageRemoteDataSource
import dk.itu.moapd.x9.s25137.domain.models.Report
import javax.inject.Inject

class ReportRepository @Inject constructor(
    private val databaseRemoteDataSource: DatabaseRemoteDataSource,
    private val imageRemoteDataSource: ImageRemoteDataSource
) {
    fun getAllQuery() =
        databaseRemoteDataSource.getAllReportsQuery()

    fun insert(report: Report, onComplete: (DatabaseError?) -> Unit): String? {
        if (report.localImageUri == null) return databaseRemoteDataSource.insertReport(
            report = report,
            onComplete = onComplete
        )

        var insertedReportKey: String? = null
        uploadImageTask(report).addOnSuccessListener { downloadUri ->
            val reportWithImage = report.copy(remoteImageUri = downloadUri.toString())
            insertedReportKey = databaseRemoteDataSource.insertReport(
                report = reportWithImage,
                onComplete = onComplete
            )
        }
        return insertedReportKey
    }

    fun update(report: Report, onComplete: (DatabaseError?) -> Unit) {
        if (report.localImageUri == null) {
            databaseRemoteDataSource.updateReport(report = report, onComplete = onComplete)
            return
        } else {
            uploadImageTask(report).addOnSuccessListener { downloadUri ->
                val reportWithImage = report.copy(remoteImageUri = downloadUri.toString())
                databaseRemoteDataSource.updateReport(
                    report = reportWithImage,
                    onComplete = onComplete
                )
            }
        }
        databaseRemoteDataSource.updateReport(report = report, onComplete = onComplete)
    }

    private fun uploadImageTask(
        report: Report
    ): Task<Uri> {
        requireNotNull(report.localImageUri) { "Report's local image URI is null." }
        return imageRemoteDataSource.uploadFile(
            localUri = report.localImageUri.toUri(),
            remotePath = "images/reports/${report.key}.jpg"
        )
    }

    fun delete(report: Report, onComplete: (DatabaseError?) -> Unit) {
        report.remoteImageUri?.let { imageRemoteDataSource.delete(it) }
        databaseRemoteDataSource.deleteReport(key = report.key!!, onComplete = onComplete)
    }
}