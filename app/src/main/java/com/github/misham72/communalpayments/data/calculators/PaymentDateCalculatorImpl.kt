package com.github.misham72.communalpayments.data.calculators


import com.github.misham72.communalpayments.domain.calculators.PaymentDateCalculator
import java.util.Calendar
import java.util.Date

class PaymentDateCalculatorImpl : PaymentDateCalculator {

    override fun getPreviousPaymentDate(monthsPeriod: Int, paymentDay: Int, startDate: Date): Date {
        val start = Calendar.getInstance().apply { time = startDate }
        val today = Calendar.getInstance()
        var periodNumber = getPeriodNumber(today, monthsPeriod, start)
        var paymentDate = getPaymentDateForPeriod(periodNumber, monthsPeriod, paymentDay, start)
        if (paymentDate.after(today)) {
            periodNumber--
            paymentDate = getPaymentDateForPeriod(periodNumber, monthsPeriod, paymentDay, start)
        }
        return paymentDate.time
    }

    override fun getNextPaymentDate(monthsPeriod: Int, paymentDay: Int, startDate: Date): Date {
        val start = Calendar.getInstance().apply { time = startDate }
        val today = Calendar.getInstance()
        var periodNumber = getPeriodNumber(today, monthsPeriod, start)
        var paymentDate = getPaymentDateForPeriod(periodNumber, monthsPeriod, paymentDay, start)
        if (paymentDate.before(today)) {
            periodNumber++
            paymentDate = getPaymentDateForPeriod(periodNumber, monthsPeriod, paymentDay, start)
        }
        return paymentDate.time
    }

    override fun getDaysFromPreviousPayment(monthsPeriod: Int, paymentDay: Int, startDate: Date): Long {
        val previous = getPreviousPaymentDate(monthsPeriod, paymentDay, startDate)
        val today = Calendar.getInstance().time
        return (today.time - previous.time) / MILLIS_IN_DAY
    }

    override fun getDaysToNextPayment(monthsPeriod: Int, paymentDay: Int, startDate: Date): Long {
        val next = getNextPaymentDate(monthsPeriod, paymentDay, startDate)
        val today = Calendar.getInstance().time
        return (next.time - today.time) / MILLIS_IN_DAY
    }

    // --- Вспомогательные методы ---
    private fun getPeriodNumber(referenceDate: Calendar, monthsPeriod: Int, start: Calendar): Int {
        val diffMonths = monthsBetween(start, referenceDate)
        return if (diffMonths >= 0) diffMonths / monthsPeriod else (diffMonths - monthsPeriod + 1) / monthsPeriod
    }

    private fun getPeriodStartByNumber(periodNumber: Int, monthsPeriod: Int, start: Calendar): Calendar {
        val result = start.clone() as Calendar
        result.add(Calendar.MONTH, periodNumber * monthsPeriod)
        return result
    }

    private fun getPaymentDateForPeriod(periodNumber: Int, monthsPeriod: Int, paymentDay: Int, start: Calendar): Calendar {
        val periodStart = getPeriodStartByNumber(periodNumber, monthsPeriod, start)
        val paymentMonth = periodStart.clone() as Calendar
        paymentMonth.add(Calendar.MONTH, monthsPeriod)
        val maxDay = paymentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
        val actualDay = if (paymentDay > maxDay) maxDay else paymentDay
        paymentMonth.set(Calendar.DAY_OF_MONTH, actualDay)
        return paymentMonth
    }

    private fun monthsBetween(start: Calendar, end: Calendar): Int {
        val startYear = start.get(Calendar.YEAR)
        val startMonth = start.get(Calendar.MONTH)
        val startDay = start.get(Calendar.DAY_OF_MONTH)
        val endYear = end.get(Calendar.YEAR)
        val endMonth = end.get(Calendar.MONTH)
        val endDay = end.get(Calendar.DAY_OF_MONTH)
        var diff = (endYear - startYear) * 12 + (endMonth - startMonth)
        if (endDay < startDay) diff--
        return diff
    }

    companion object {
        private const val MILLIS_IN_DAY = 24 * 60 * 60 * 1000L
    }
}