package dk.itu.moapd.x9.s25137.data.repositories

import dk.itu.moapd.x9.s25137.data.datasources.ReportRemoteDataSource
import dk.itu.moapd.x9.s25137.domain.models.Report

class ReportRepository(
    private val reportRemoteDataSource: ReportRemoteDataSource = ReportRemoteDataSource()
) {
    fun getAllQuery() =
        reportRemoteDataSource.getAllQuery()

    fun insert(report: Report): String? =
        reportRemoteDataSource.insert(report = report)

    fun update(report: Report) =
        reportRemoteDataSource.update(report = report)

    fun delete(key: String) =
        reportRemoteDataSource.delete(key = key)
}