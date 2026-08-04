package com.github.misham72.communalpayments.domain.calculators

import com.github.misham72.communalpayments.domain.model.MeterResult

interface MeterCalculator {
    fun calculate(current: Double, previous: Double, tariff: Double): MeterResult
}
