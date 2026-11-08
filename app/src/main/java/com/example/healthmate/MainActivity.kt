package com.example.healthmate

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: HealthRecordViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val database = HealthMateDatabase.getDatabase(this)
        val repository = HealthRecordRepository(database.healthRecordDao())
        val factory = HealthRecordViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[HealthRecordViewModel::class.java]
        loadDashboardData()

        val btnAddRecord = findViewById<Button>(R.id.btnAddRecord)
        val btnViewRecords = findViewById<Button>(R.id.btnViewRecords)

        btnViewRecords.setOnClickListener {
            val intent = Intent(this, ViewRecordsActivity::class.java)
            startActivity(intent)
        }

        btnAddRecord.setOnClickListener {
            val intent = Intent(this, AddRecordActivity::class.java)
            startActivity(intent)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }
    private fun loadDashboardData() {
        val tvStepsValue = findViewById<TextView>(R.id.tvStepsValue)
        val tvCaloriesValue = findViewById<TextView>(R.id.tvCaloriesValue)
        val tvWaterValue = findViewById<TextView>(R.id.tvWaterValue)
        val today = LocalDate.now()
        val dateFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")
        val todayDate = today.format(dateFormatter)
        lifecycleScope.launch {
            val allRecords = viewModel.getDashboardRecords()

            val groupedRecords = allRecords.groupBy { it.date }

            val selectedDate = if (groupedRecords.containsKey(todayDate)) {
                todayDate
            } else {
                allRecords.maxByOrNull {
                    LocalDate.parse(it.date, dateFormatter)
                }?.date
            }

            val selectedRecords = selectedDate?.let {
                groupedRecords[it]
            }.orEmpty()

            val totalSteps = selectedRecords.sumOf { it.steps }
            val totalCalories = selectedRecords.sumOf { it.calories }
            val totalWater = selectedRecords.sumOf { it.water }

            tvStepsValue.text = totalSteps.toString()
            tvCaloriesValue.text = "$totalCalories kcal"
            tvWaterValue.text = "$totalWater ml"
        }

    }
    override fun onResume() {
        super.onResume()
        loadDashboardData()
    }

}