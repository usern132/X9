package dk.itu.moapd.x9.s25137.data.repositories

import dk.itu.moapd.x9.s25137.data.datasources.ReportRemoteDataSource
import dk.itu.moapd.x9.s25137.domain.models.Report

class ReportRepository(
    private val reportRemoteDataSource: ReportRemoteDataSource = ReportRemoteDataSource()
) {
    fun reportsQuery(userId: String) =
        reportRemoteDataSource.reportsQuery(userId = userId)

    fun insert(userId: String, report: Report) =
        reportRemoteDataSource.insert(userId = userId, report = report)

    fun update(userId: String, key: String, report: Report) =
        reportRemoteDataSource.update(userId = userId, key = key, report = report)

    fun delete(userId: String, key: String) =
        reportRemoteDataSource.delete(userId = userId, key = key)
}