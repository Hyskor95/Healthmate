package com.example.healthmate
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
@Dao
interface HealthRecordDao {
    @Insert
    suspend fun insertRecord(record: HealthRecord)
    @Query("SELECT * FROM health_records ORDER BY date DESC")
    suspend fun getAllRecords(): List<HealthRecord>
    @Update
    suspend fun updateRecord(record: HealthRecord)
    @Delete
    suspend fun deleteRecord(record: HealthRecord)
    @Query("SELECT * FROM health_records WHERE date = :recordDate")
    suspend fun searchRecordsByDate(recordDate: String): List<HealthRecord>
}