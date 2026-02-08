package dk.itu.moapd.x9.s25137

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.datepicker.MaterialDatePicker
import dk.itu.moapd.x9.s25137.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val reportTitleInput = binding.reportTitleInput
        val reportLocationInput = binding.reportLocationInput
        val reportDateInput = binding.reportDateInput
        val reportTypeInput = binding.reportTypeInput
        val reportDescriptionInput = binding.reportDescriptionInput
        val reportSeverityRadioGroup = binding.reportSeverityRadioGroup
        val submitButton = binding.submitButton

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
}