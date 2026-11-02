package com.github.misham72.communalpayments.presentation.screen.screens.gas

import com.github.misham72.communalpayments.domain.model.metric.GasNormData
import com.github.misham72.communalpayments.domain.model.metric.MeterData

sealed interface DisplayResult {
    data class Meter(val data: MeterData) : DisplayResult
    data class Norm(val data: GasNormData) : DisplayResult
}
