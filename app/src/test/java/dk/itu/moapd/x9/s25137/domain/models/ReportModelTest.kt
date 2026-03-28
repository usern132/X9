package dk.itu.moapd.x9.s25137.domain.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReportModelTest {

    @Test
    fun generateRandomReportsReturnsRequestedSize() {
        val reports = Report.generateRandomReports(12)

        assertEquals(12, reports.size)
    }

    @Test
    fun generateRandomReportsCreatesReportKeys() {
        val reports = Report.generateRandomReports(20)

        assertTrue(reports.all { !it.key.isNullOrBlank() })
    }

    @Test
    fun reportDefaultsMatchExpectedEmptyState() {
        val report = Report()

        assertEquals(null, report.key)
        assertEquals("", report.title)
        assertEquals("", report.location)
        assertEquals(0L, report.timestamp)
        assertEquals(Type.OTHER, report.type)
        assertEquals("", report.description)
        assertEquals(Severity.MINOR, report.severity)
    }
}

