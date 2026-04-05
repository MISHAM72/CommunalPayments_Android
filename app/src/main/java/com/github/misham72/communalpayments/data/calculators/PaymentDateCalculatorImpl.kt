package com.github.misham72.communalpayments.data.calculators


import com.github.misham72.communalpayments.domain.calculators.PaymentDateCalculator
import java.util.Calendar
import java.util.Date

class PaymentDateCalculatorImpl : PaymentDateCalculator {

    override fun getPreviousPaymentDate(monthsPeriod: Int, paymentDay: Int, startDate: Date): Date {
        val today = Calendar.getInstance()
        val start = Calendar.getInstance().apply { time = startDate }
        return alignToPeriodStart(today, monthsPeriod, paymentDay, start).time
    }

    override fun getNextPaymentDate(monthsPeriod: Int, paymentDay: Int, startDate: Date): Date {
        val today = Calendar.getInstance()
        val start = Calendar.getInstance().apply { time = startDate }
        return alignToPeriodEnd(today, monthsPeriod, paymentDay, start).time
    }

    override fun getDaysFromPreviousPayment(monthsPeriod: Int, paymentDay: Int, startDate: Date): Long {
        val today = Calendar.getInstance()
        val start = Calendar.getInstance().apply { time = startDate }
        val previous = alignToPeriodStart(today, monthsPeriod, paymentDay, start)
        return (today.timeInMillis - previous.timeInMillis) / MILLIS_IN_DAY
    }

    override fun getDaysToNextPayment(monthsPeriod: Int, paymentDay: Int, startDate: Date): Long {
        val today = Calendar.getInstance()
        val start = Calendar.getInstance().apply { time = startDate }
        val next = alignToPeriodEnd(today, monthsPeriod, paymentDay, start)
        return (next.timeInMillis - today.timeInMillis) / MILLIS_IN_DAY
    }

    // --- Вспомогательные методы ---
    private fun alignToPeriodStart(today: Calendar, monthsPeriod: Int, paymentDay: Int, start: Calendar): Calendar {
        val currentPayment = getPaymentDateForPeriod(today, monthsPeriod, paymentDay, start)
        return if (today.before(currentPayment)) {
            val prevPeriodStart = getPeriodStart(today, monthsPeriod, start).apply {
                add(Calendar.MONTH, -monthsPeriod)
            }
            getPaymentDateForPeriod(prevPeriodStart, monthsPeriod, paymentDay, start)
        } else {
            currentPayment
        }
    }

    private fun alignToPeriodEnd(today: Calendar, monthsPeriod: Int, paymentDay: Int, start: Calendar): Calendar {
        val currentPayment = getPaymentDateForPeriod(today, monthsPeriod, paymentDay, start)
        return if (today.before(currentPayment)) {
            currentPayment
        } else {
            val nextPeriodStart = getPeriodStart(today, monthsPeriod, start).apply {
                add(Calendar.MONTH, monthsPeriod)
            }
            getPaymentDateForPeriod(nextPeriodStart, monthsPeriod, paymentDay, start)
        }
    }

    private fun getPaymentDateForPeriod(referenceDate: Calendar, monthsPeriod: Int, paymentDay: Int, start: Calendar): Calendar {
        val periodNumber = getPeriodNumber(referenceDate, monthsPeriod, start)
        val periodStart = getPeriodStartByNumber(periodNumber, monthsPeriod, start)
        val paymentMonth = periodStart.clone() as Calendar
        // 👇 ВАЖНО: добавляем monthsPeriod, а не monthsPeriod - 1
        paymentMonth.add(Calendar.MONTH, monthsPeriod)
        val maxDay = paymentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
        val actualDay = if (paymentDay > maxDay) maxDay else paymentDay
        paymentMonth.set(Calendar.DAY_OF_MONTH, actualDay)
        return paymentMonth
    }

    private fun getPeriodNumber(referenceDate: Calendar, monthsPeriod: Int, start: Calendar): Int {
        val diffMonths = monthsBetween(start, referenceDate)
        return if (diffMonths >= 0) diffMonths / monthsPeriod else (diffMonths - monthsPeriod + 1) / monthsPeriod
    }

    private fun getPeriodStartByNumber(periodNumber: Int, monthsPeriod: Int, start: Calendar): Calendar {
        val result = start.clone() as Calendar
        result.add(Calendar.MONTH, periodNumber * monthsPeriod)
        return result
    }

    private fun getPeriodStart(referenceDate: Calendar, monthsPeriod: Int, start: Calendar): Calendar {
        val periodNumber = getPeriodNumber(referenceDate, monthsPeriod, start)
        return getPeriodStartByNumber(periodNumber, monthsPeriod, start)
    }
    private fun monthsBetween(start: Calendar, end: Calendar): Int {
        val startYear = start.get(Calendar.YEAR)
        val startMonth = start.get(Calendar.MONTH)
        val startDay = start.get(Calendar.DAY_OF_MONTH)
        val endYear = end.get(Calendar.YEAR)
        val endMonth = end.get(Calendar.MONTH)
        val endDay = end.get(Calendar.DAY_OF_MONTH)
        var diff = (endYear - startYear) * 12 + (endMonth - startMonth)
        if (endDay < startDay) {
            diff--   // неполный месяц
        }
        return diff
    }
    /**private fun monthsBetween(start: Calendar, end: Calendar): Int {
        val startYear = start.get(Calendar.YEAR)
        val startMonth = start.get(Calendar.MONTH)
        val endYear = end.get(Calendar.YEAR)
        val endMonth = end.get(Calendar.MONTH)
        return (endYear - startYear) * 12 + (endMonth - startMonth)
    }*/

    companion object {
        private const val MILLIS_IN_DAY = 24 * 60 * 60 * 1000L
    }
}
