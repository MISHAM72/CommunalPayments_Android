package com.github.misham72.communalpayments.data.calculators

import com.github.misham72.communalpayments.domain.calculators.MeterCalculator
import com.github.misham72.communalpayments.domain.model.MeterResult

class MeterCalculatorImpl : MeterCalculator {
    override fun calculate(current: Double, previous: Double, tariff: Double): MeterResult {
        val consumption = current - previous
        val payment = consumption * tariff
        return MeterResult(consumption, payment)
    }
}
