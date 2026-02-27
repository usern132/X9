package dk.itu.moapd.x9.s25137

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import io.bloco.faker.Faker

private val TAG = "MainActivityViewModel"

class MainActivityViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    val trafficReports = mutableListOf<Report>()
    val faker = Faker()

    init {
        for (i in 1..100) {
            val trafficReport = Report(
                title = faker.lorem.sentence(wordCount = 3),
                location = faker.address.streetAddress(),
                date = faker.date.backward(),
                type = Type.entries.random(),
                description = faker.lorem.paragraphs(1).toString(),
                severity = Severity.entries.random()
            )
            trafficReports.add(trafficReport)
        }
        Log.d(TAG, trafficReports.toString())
    }
}