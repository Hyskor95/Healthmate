package com.example.healthmate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HealthRecordViewModelFactory(
    private val repository: HealthRecordRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HealthRecordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HealthRecordViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }

}