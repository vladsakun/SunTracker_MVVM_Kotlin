package com.example.suntracker.ui.suninfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.suntracker.data.repository.SunInfoRepository

class CurrentSunInfoViewModelFactory(
    private val sunInfoRepository: SunInfoRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CurrentSunInfoViewModel(sunInfoRepository) as T
    }
}
