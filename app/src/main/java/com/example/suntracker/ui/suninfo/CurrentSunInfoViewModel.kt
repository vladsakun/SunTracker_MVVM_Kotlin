package com.example.suntracker.ui.suninfo

import androidx.lifecycle.ViewModel
import com.example.suntracker.data.repository.SunInfoRepository
import com.example.suntracker.internal.lazyDeferred

class CurrentSunInfoViewModel(
    private val sunInfoRepository: SunInfoRepository
) : ViewModel() {

    val sunInfo by lazyDeferred {
        sunInfoRepository.getCurrentSunInfo()
    }

    val sunLocation by lazyDeferred {
        sunInfoRepository.getSunLocation()
    }
}
