package dk.itu.moapd.x9.s25137.ui.calendar

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dk.itu.moapd.x9.s25137.common.Utils.timestampToLocalDate
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.ui.reports.list.ReportList
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.day.DayState
import io.github.boguszpawlowski.composecalendar.day.DefaultDay
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import io.github.boguszpawlowski.composecalendar.selection.DynamicSelectionState

@Composable
fun CalendarPage(
    reports: List<Report>,
    onDeleteReport: (String) -> Unit = {},
    isReportDeletable: (Report) -> Boolean = { false },
    onItemClick: (String) -> Unit = {}
) {
    Column {
        val calendarState = rememberSelectableCalendarState()
        val selectedDayReports = reports.filter { report ->
            calendarState.selectionState.isDateSelected(
                timestampToLocalDate(report.timestamp)
            )
        }
        SelectableCalendar(
            calendarState = calendarState,
            dayContent = { dayState ->
                MyDay(
                    dayState,
                    hasReports = reports.any { report ->
                        dayState.date ==
                                timestampToLocalDate(report.timestamp)
                    }
                )
            }
        )
        ReportList(
            reports = selectedDayReports,
            onDeleteReport = onDeleteReport,
            isReportDeletable = isReportDeletable,
            onItemClick = onItemClick
        )
    }
}

@Composable
private fun MyDay(dayState: DayState<DynamicSelectionState>, hasReports: Boolean) {
    DefaultDay(
        state = dayState,
        modifier = if (hasReports) Modifier.border(
            width = 1.dp,
            color = Color.Red.copy(alpha = 0.5f),
            shape = RoundedCornerShape(4.dp)
        ) else Modifier,
    )
}

@Composable
@Preview(showBackground = true)
private fun CalendarPagePreview() {
    CalendarPage(
        reports = Report.previewReports
    )
}