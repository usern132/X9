package dk.itu.moapd.x9.s25137.data.repositories

import com.google.firebase.database.DatabaseError
import dk.itu.moapd.x9.s25137.data.datasources.ReportRemoteDataSource
import dk.itu.moapd.x9.s25137.domain.models.Report
import javax.inject.Inject

class ReportRepository @Inject constructor(
    private val reportRemoteDataSource: ReportRemoteDataSource
) {
    fun getAllQuery() =
        reportRemoteDataSource.getAllQuery()

    fun insert(report: Report, onComplete: (DatabaseError?) -> Unit): String? =
        reportRemoteDataSource.insert(report = report, onComplete = onComplete)

    fun update(report: Report, onComplete: (DatabaseError?) -> Unit) =
        reportRemoteDataSource.update(report = report, onComplete = onComplete)

    fun delete(key: String, onComplete: (DatabaseError?) -> Unit) =
        reportRemoteDataSource.delete(key = key, onComplete = onComplete)
}