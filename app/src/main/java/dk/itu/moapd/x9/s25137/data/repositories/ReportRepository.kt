package dk.itu.moapd.x9.s25137.data.repositories

import android.net.Uri
import androidx.core.net.toUri
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseError
import dk.itu.moapd.x9.s25137.data.datasources.DatabaseRemoteDataSource
import dk.itu.moapd.x9.s25137.data.datasources.StorageRemoteDataSource
import dk.itu.moapd.x9.s25137.domain.models.Report
import javax.inject.Inject

class ReportRepository @Inject constructor(
    private val databaseRemoteDataSource: DatabaseRemoteDataSource,
    private val storageRemoteDataSource: StorageRemoteDataSource
) {
    fun getAllQuery() =
        databaseRemoteDataSource.getAllReportsQuery()

    fun insert(report: Report, onComplete: (DatabaseError?) -> Unit): String? {
        val insertedReportKey = databaseRemoteDataSource.insertReport(
            report = report,
            onComplete = onComplete
        )
        if (report.localImageUri != null) {
            val reportWithKey = report.copy(key = insertedReportKey)
            uploadReportImageTask(reportWithKey).addOnSuccessListener { downloadUri ->
                val reportWithImage = reportWithKey.copy(remoteImageUri = downloadUri.toString())
                databaseRemoteDataSource.updateReport(
                    report = reportWithImage,
                    onComplete = onComplete
                )
            }
        }
        return insertedReportKey
    }

    fun update(report: Report, onComplete: (DatabaseError?) -> Unit) {
        databaseRemoteDataSource.updateReport(report = report, onComplete = onComplete)
        if (report.localImageUri != null) {
            uploadReportImageTask(report).addOnSuccessListener { downloadUri ->
                val reportWithImage = report.copy(remoteImageUri = downloadUri.toString())
                databaseRemoteDataSource.updateReport(
                    report = reportWithImage,
                    onComplete = onComplete
                )
            }
        }
    }

    fun delete(report: Report, onComplete: (DatabaseError?) -> Unit) {
        report.remoteImageUri?.let { storageRemoteDataSource.deleteFile(it) }
        databaseRemoteDataSource.deleteReport(key = report.key!!, onComplete = onComplete)
    }

    private fun uploadReportImageTask(
        report: Report
    ): Task<Uri> {
        requireNotNull(report.localImageUri) { "Report's local image URI is null." }
        return storageRemoteDataSource.uploadFile(
            localUri = report.localImageUri.toUri(),
            remotePath = "images/reports/${report.key}.jpg"
        )
    }
}