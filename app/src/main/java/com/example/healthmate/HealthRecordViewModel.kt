package com.example.healthmate
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HealthRecordViewModel(
    private val repository: HealthRecordRepository
) : androidx.lifecycle.ViewModel() {
    private val _records = MutableStateFlow<List<HealthRecord>>(emptyList())
    val records: StateFlow<List<HealthRecord>> = _records
    suspend fun getDashboardRecords(): List<HealthRecord> {
        return repository.getAllRecords()
    }
    suspend fun loadRecords(): List<HealthRecord> {
        val allRecords = repository.getAllRecords()
        _records.value = allRecords
        return allRecords

    }
        suspend fun insertRecord(record: HealthRecord) {
            repository.insertRecord(record)
            loadRecords()
        }
        suspend fun updateRecord(record: HealthRecord) {
            repository.updateRecord(record)
            loadRecords()
        }
    suspend fun deleteRecord(record: HealthRecord) {
        repository.deleteRecord(record)
        loadRecords()
    }
    suspend fun searchRecordsByDate(recordDate: String): List<HealthRecord> {
        val searchResults = repository.searchRecordsByDate(recordDate)
        _records.value = searchResults
        return searchResults
    }



}