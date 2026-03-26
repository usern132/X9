package dk.itu.moapd.x9.s25137.data.repositories

import dk.itu.moapd.x9.s25137.data.datasources.ReportRemoteDataSource
import dk.itu.moapd.x9.s25137.domain.models.Report

class ReportRepository(
    private val reportRemoteDataSource: ReportRemoteDataSource = ReportRemoteDataSource()
) {
    fun getAllQuery(userId: String) =
        reportRemoteDataSource.getAllQuery(userId = userId)

    fun insert(userId: String, report: Report): String? =
        reportRemoteDataSource.insert(userId = userId, report = report)

    fun update(userId: String, report: Report) =
        reportRemoteDataSource.update(userId = userId, report = report)

    fun delete(userId: String, key: String) =
        reportRemoteDataSource.delete(userId = userId, key = key)
}