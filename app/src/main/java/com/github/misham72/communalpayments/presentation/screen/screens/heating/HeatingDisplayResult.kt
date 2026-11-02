package com.github.misham72.communalpayments.presentation.screen.screens.heating

import com.github.misham72.communalpayments.domain.model.metric.HeatingMeterData
import com.github.misham72.communalpayments.domain.model.periodic.PeriodicData

sealed interface HeatingDisplayResult {
    data class Meter(val data: HeatingMeterData) : HeatingDisplayResult
    data class Period(val data: PeriodicData) : HeatingDisplayResult
}
