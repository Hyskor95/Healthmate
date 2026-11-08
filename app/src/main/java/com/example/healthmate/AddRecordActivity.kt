package com.example.healthmate

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider
class AddRecordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_record)
        val database = HealthMateDatabase.getDatabase(this)
        val repository = HealthRecordRepository(database.healthRecordDao())
        val factory = HealthRecordViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[HealthRecordViewModel::class.java]
        val etRecordDate = findViewById<EditText>(R.id.etRecordDate)
        val etSteps = findViewById<EditText>(R.id.etSteps)
        val etCalories = findViewById<EditText>(R.id.etCalories)
        val etWater = findViewById<EditText>(R.id.etWater)
        val recordId = intent.getIntExtra("record_id", -1)
        if (recordId != -1) {
            etRecordDate.setText(intent.getStringExtra("record_date"))
            etSteps.setText(intent.getIntExtra("record_steps", 0).toString())
            etCalories.setText(intent.getIntExtra("record_calories", 0).toString())
            etWater.setText(intent.getIntExtra("record_water", 0).toString())
        }

        val btnSaveRecord = findViewById<Button>(R.id.btnSaveRecord)
        btnSaveRecord.setOnClickListener {val recordDate = etRecordDate.text.toString()
            val steps = etSteps.text.toString()
            val calories = etCalories.text.toString()
            val water = etWater.text.toString()
            if (recordDate.isBlank() || steps.isBlank() || calories.isBlank() || water.isBlank()) {
                Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val stepsValue = steps.toIntOrNull()
            val caloriesValue = calories.toIntOrNull()
            val waterValue = water.toIntOrNull()
            if (stepsValue == null || caloriesValue == null || waterValue == null ||
                stepsValue < 0 || stepsValue > 100000 ||
                caloriesValue < 0 || caloriesValue > 10000 ||
                waterValue < 0 || waterValue > 20000) {

                Toast.makeText(this, "Please enter valid non-negative numbers", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val healthRecord = HealthRecord(
                id = if (recordId != -1) recordId else 0,
                date = recordDate,
                steps = stepsValue,
                calories = caloriesValue,
                water = waterValue
            )
            lifecycleScope.launch {
                if (recordId != -1) {
                    viewModel.updateRecord(healthRecord)
                } else {
                    viewModel.insertRecord(healthRecord)
                }

                Toast.makeText(this@AddRecordActivity, "Record saved successfully", Toast.LENGTH_SHORT).show()
                finish()
            }

        }
        etRecordDate.setOnClickListener {val calendar = java.util.Calendar.getInstance()

            val year = calendar.get(java.util.Calendar.YEAR)
            val month = calendar.get(java.util.Calendar.MONTH)
            val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

            val datePicker = android.app.DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->

                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    etRecordDate.setText(selectedDate)

                },
                year,
                month,
                day
            )

            datePicker.show()

        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}