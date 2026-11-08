package com.example.healthmate
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_records")
class HealthRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    val steps: Int,
    val calories: Int,
    val water: Int
)