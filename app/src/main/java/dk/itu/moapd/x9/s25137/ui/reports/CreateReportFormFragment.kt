package dk.itu.moapd.x9.s25137.ui.reports

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dk.itu.moapd.x9.s25137.databinding.FragmentCreateReportFormBinding
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.domain.models.Severity
import dk.itu.moapd.x9.s25137.domain.models.Type
import dk.itu.moapd.x9.s25137.ui.main.MainActivityViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "CreateReportFormFragment"

class CreateReportFormFragment : Fragment() {
    private var _binding: FragmentCreateReportFormBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var reportTitleInput: TextInputEditText
    private lateinit var reportTitleInputLayout: TextInputLayout
    private lateinit var reportLocationInput: TextInputEditText
    private lateinit var reportLocationInputLayout: TextInputLayout
    private lateinit var reportDateInput: TextInputEditText
    private lateinit var reportDateInputLayout: TextInputLayout
    private lateinit var reportTypeInput: AutoCompleteTextView
    private lateinit var reportTypeInputLayout: TextInputLayout
    private lateinit var reportDescriptionInput: TextInputEditText
    private lateinit var reportDescriptionInputLayout: TextInputLayout
    private lateinit var reportSeverityRadioGroup: RadioGroup
    private lateinit var submitButton: Button

    private var savedReport: Report? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView() called")
        _binding = FragmentCreateReportFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated() called")

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
            datePicker.show(parentFragmentManager, "datePicker")
            datePicker.addOnPositiveButtonClickListener { selection ->
                reportDate = Date(selection)
                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.reportDateInput.setText(format.format(reportDate))
            }
        }

        val typeNames = Type.entries.map { getString(it.nameResId) }
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_list_item_1, typeNames)
        binding.reportTypeInput.setAdapter(adapter)

        submitButton.setOnClickListener {
            val areThereEmptyFields = checkForEmptyFields()
            if (areThereEmptyFields) return@setOnClickListener

            savedReport = Report(
                title = reportTitleInput.text.toString(),
                location = reportLocationInput.text.toString(),
                date = reportDate,
                type = Type.entries.find {
                    getString(it.nameResId) == reportTypeInput.text.toString()
                } ?: Type.OTHER,
                description = reportDescriptionInput.text.toString(),
                severity = when (reportSeverityRadioGroup.checkedRadioButtonId) {
                    dk.itu.moapd.x9.s25137.R.id.minor_button -> Severity.MINOR
                    dk.itu.moapd.x9.s25137.R.id.moderate_button -> Severity.MODERATE
                    else -> Severity.MAJOR
                }
            )
            savedReport?.let { mainActivityViewModel.reports.add(it) }
            Log.d(TAG, "Report saved successfully!\n$savedReport")
            Toast.makeText(
                context,
                dk.itu.moapd.x9.s25137.R.string.report_saved,
                Toast.LENGTH_SHORT
            ).show()
            findNavController().popBackStack()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() called")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView() called")
        _binding = null
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
                layout.error = getString(dk.itu.moapd.x9.s25137.R.string.field_is_required)
            } else {
                layout.error = null
            }
        }
        return areThereEmptyFields
    }
}
