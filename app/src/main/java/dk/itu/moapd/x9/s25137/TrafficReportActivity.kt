package dk.itu.moapd.x9.s25137

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dk.itu.moapd.x9.s25137.databinding.ActivityTrafficReportBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val TAG = "TrafficReportActivity"

class TrafficReportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrafficReportBinding
    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    private lateinit var reportTitleInput: TextInputEditText
    private lateinit var reportTitleInputLayout: TextInputLayout
    private lateinit var reportLocationInput: TextInputEditText
    private lateinit var reportLocationInputLayout: TextInputLayout
    private lateinit var reportDateInput: TextInputEditText
    private lateinit var reportDateInputLayout: TextInputLayout
    private lateinit var reportTypeInput: AutoCompleteTextView
    private lateinit var reportTypeInputLayout: TextInputLayout // ???
    private lateinit var reportDescriptionInput: TextInputEditText
    private lateinit var reportDescriptionInputLayout: TextInputLayout
    private lateinit var reportSeverityRadioGroup: RadioGroup
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTrafficReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.traffic_report)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        reportTitleInput = binding.reportTitleInput
        reportTitleInputLayout = binding.reportTitleInputLayout
        reportLocationInput = binding.reportLocationInput
        reportLocationInputLayout = binding.reportLocationInputLayout
        reportDateInput = binding.reportDateInput
        reportDateInputLayout = binding.reportDateInputLayout
        reportTypeInput = binding.reportTypeInput
        reportTypeInputLayout = binding.reportTypeInputLayout
        reportDescriptionInput = binding.reportDescriptionInput
        reportDescriptionInputLayout = binding.reportDescriptionInputLayout
        reportSeverityRadioGroup = binding.reportSeverityRadioGroup
        submitButton = binding.submitButton

        var reportDate = Date(System.currentTimeMillis())

        reportDateInput.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.show(supportFragmentManager, "datePicker")
            datePicker.addOnPositiveButtonClickListener { selection ->
                reportDate = Date(selection)
                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.reportDateInput.setText(format.format(reportDate))
            }
        }

        val items = resources.getStringArray(R.array.report_types)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        binding.reportTypeInput.setAdapter(adapter)

        submitButton.setOnClickListener {
            val areThereEmptyFields = checkForEmptyFields()
            if (areThereEmptyFields) return@setOnClickListener

            val savedReport = Report(
                title = reportTitleInput.text.toString(),
                location = reportLocationInput.text.toString(),
                date = reportDate,
                type = when (reportTypeInput.text.toString()) {
                    items[0] -> Type.SPEED_CAMERA
                    items[1] -> Type.HEAVY_TRAFFIC
                    items[2] -> Type.ROAD_INCIDENTS
                    items[3] -> Type.BROKEN_VEHICLES
                    else -> Type.OTHER
                },
                description = reportDescriptionInput.text.toString(),
                severity = when (reportSeverityRadioGroup.checkedRadioButtonId) {
                    R.id.minor_button -> Severity.MINOR
                    R.id.moderate_button -> Severity.MODERATE
                    else -> Severity.MAJOR
                }
            )
            Log.d(TAG, "Report saved successfully!\n$savedReport")
            AlertDialog.Builder(this).setTitle("Report").setMessage(savedReport.toString())
                .setPositiveButton("OK") { _, _ -> }.show()
        }
    }

    private fun checkForEmptyFields(): Boolean {
        val fields = listOf(
            Pair(reportTitleInput, reportTitleInputLayout),
            Pair(reportLocationInput, reportLocationInputLayout),
            Pair(reportDateInput, reportDateInputLayout),
            Pair(reportTypeInput, reportTypeInputLayout),
            Pair(reportDescriptionInput, reportDescriptionInputLayout)
        )
        var areThereEmptyFields = false
        for ((input, layout) in fields) {
            if (input.text.toString().isBlank()) {
                areThereEmptyFields = true
                layout.error = getString(R.string.field_is_required)
            } else {
                layout.error = null
            }
        }
        return areThereEmptyFields
    }
}