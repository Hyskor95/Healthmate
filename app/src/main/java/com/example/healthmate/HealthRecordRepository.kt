package com.example.healthmate

class HealthRecordRepository(
    private val healthRecordDao: HealthRecordDao
) {
    suspend fun getAllRecords(): List<HealthRecord> {
        return healthRecordDao.getAllRecords()
    }
    suspend fun insertRecord(record: HealthRecord) {
        healthRecordDao.insertRecord(record)
    }
    suspend fun updateRecord(record: HealthRecord) {
        healthRecordDao.updateRecord(record)
    }
    suspend fun deleteRecord(record: HealthRecord) {
        healthRecordDao.deleteRecord(record)
    }
    suspend fun searchRecordsByDate(recordDate: String): List<HealthRecord> {
        return healthRecordDao.searchRecordsByDate(recordDate)
    }
}