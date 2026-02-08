package dk.itu.moapd.x9.s25137

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.datepicker.MaterialDatePicker
import dk.itu.moapd.x9.s25137.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val reportDateInput = binding.reportDateInput
    private val reportTypeInput = binding.reportTypeInput
    private val reportTitleInput = binding.reportTitleInput
    private val reportLocationInput = binding.reportLocationInput
    private val reportDescriptionInput = binding.reportDescriptionInput
    private val severityRadioGroup = binding.severityRadioGroup
    private val submitButton = binding.submitButton

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

        reportDateInput.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.show(supportFragmentManager, "datePicker")
            datePicker.addOnPositiveButtonClickListener { selection ->
                val date = Date(selection)
                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.reportDateInput.setText(format.format(date))
            }
        }

        val items = resources.getStringArray(R.array.report_types)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        binding.reportTypeInput.setAdapter(adapter)
    }
}