package com.example.healthmate

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.Toast
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.Button
import android.content.Intent
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.collect

class ViewRecordsActivity : AppCompatActivity() {
    private lateinit var viewModel: HealthRecordViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_records)
        val database = HealthMateDatabase.getDatabase(this)
        val repository = HealthRecordRepository(database.healthRecordDao())
        val factory = HealthRecordViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[HealthRecordViewModel::class.java]
        lifecycleScope.launch {
            viewModel.records.collect { records ->
                displayRecords(records)
            }
        }
        val healthRecordDao = database.healthRecordDao()
        val etSearchDate = findViewById<EditText>(R.id.etSearchDate)
        val btnSearchDate = findViewById<Button>(R.id.btnSearchDate)
        val btnClearSearch = findViewById<Button>(R.id.btnClearSearch)
        btnSearchDate.setOnClickListener {
            val searchDate = etSearchDate.text.toString().trim()

            if (searchDate.isBlank()) {
                Toast.makeText(this, "Please enter a date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loadRecords(searchDate)
        }

        btnClearSearch.setOnClickListener {
            etSearchDate.text.clear()
            loadRecords()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun loadRecords(searchDate: String? = null) {

        lifecycleScope.launch {

            if (searchDate.isNullOrBlank()) {
                viewModel.loadRecords()
            } else {
                viewModel.searchRecordsByDate(searchDate)
            }

        }

    }
    private fun displayRecords(records: List<HealthRecord>) {

        val recordsContainer = findViewById<LinearLayout>(R.id.recordsContainer)
        recordsContainer.removeAllViews()
        for (record in records) {

            val recordText = TextView(this@ViewRecordsActivity)

            recordText.text = "Date: ${record.date}\nSteps: ${record.steps}\nCalories: ${record.calories} kcal\nWater: ${record.water} ml"

            recordText.setTextColor(android.graphics.Color.WHITE)
            recordText.textSize = 16f
            recordText.setPadding(24, 24, 24, 24)

            recordsContainer.addView(recordText)
            val btnEdit = Button(this@ViewRecordsActivity)
            btnEdit.text = "Edit"
            recordsContainer.addView(btnEdit)

            btnEdit.setOnClickListener {
                val intent = Intent(this@ViewRecordsActivity, AddRecordActivity::class.java)

                intent.putExtra("record_id", record.id)
                intent.putExtra("record_date", record.date)
                intent.putExtra("record_steps", record.steps)
                intent.putExtra("record_calories", record.calories)
                intent.putExtra("record_water", record.water)

                startActivity(intent)
            }

            val btnDelete = Button(this@ViewRecordsActivity)
            btnDelete.text = "Delete"

            btnDelete.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.RED
                )
            )

            btnDelete.setTextColor(android.graphics.Color.WHITE)
            recordsContainer.addView(btnDelete)

            btnDelete.setOnClickListener {
                lifecycleScope.launch {
                    viewModel.deleteRecord(record)
                }
            }
            }

        }


    override fun onResume() {
        super.onResume()
        loadRecords()
    }
}