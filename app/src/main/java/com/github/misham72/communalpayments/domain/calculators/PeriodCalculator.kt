package com.github.misham72.communalpayments.domain.calculators

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/** Вычисляет предыдущие/следующие даты платежей. Считает количество дней между датами
Работает с календарем и временными промежутками. Форматирует даты в строки */
class PeriodCalculator {

    companion object {
        // ===== Публичные методы (без изменений) =====
        fun getPreviousPaymentString(monthsPeriod: Int, paymentDay: Int): String {
            val date = getPreviousPayment(monthsPeriod, paymentDay)
            return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date)
        }

        fun getNextPaymentString(monthsPeriod: Int, paymentDay: Int): String {
            val date = getNextPayment(monthsPeriod, paymentDay)
            return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date)
        }

        // ===== Приватные методы, которые нужно сохранить (исправлены) =====
        private fun getPreviousPayment(monthsPeriod: Int, paymentDay: Int): Date {
            val calendar = Calendar.getInstance()
            return alignToPeriodStart(calendar, monthsPeriod, paymentDay).time
        }

        private fun getNextPayment(monthsPeriod: Int, paymentDay: Int): Date {
            val calendar = Calendar.getInstance()
            return alignToPeriodEnd(calendar, monthsPeriod, paymentDay).time
        }

        // ===== Основные расчётные методы (должны остаться, исправлены) =====
        fun calculateDaysFromPreviousPayment(monthsPeriod: Int, paymentDay: Int): Long {
            val today = Calendar.getInstance()
            val previousPayment = alignToPeriodStart(today.clone() as Calendar, monthsPeriod, paymentDay)
            val diff = today.timeInMillis - previousPayment.timeInMillis
            return diff / (24 * 60 * 60 * 1000)
        }

        fun calculateDaysToNextPayment(monthsPeriod: Int, paymentDay: Int): Long {
            val today = Calendar.getInstance()
            val nextPayment = alignToPeriodEnd(today.clone() as Calendar, monthsPeriod, paymentDay)
            val diff = nextPayment.timeInMillis - today.timeInMillis
            return diff / (24 * 60 * 60 * 1000)
        }

        // ===== Исправленные align-методы (сохранены имена) =====
        private fun alignToPeriodStart(today: Calendar, monthsPeriod: Int, paymentDay: Int): Calendar {
            // Вычисляем дату платежа в текущем периоде
            val currentPayment = getPaymentDateForPeriod(today, monthsPeriod, paymentDay)

            return if (today.before(currentPayment)) {
                // Если сегодня раньше платежа в текущем периоде, предыдущий платёж был в предыдущем периоде
                val prevPeriodStart = getPeriodStart(today, monthsPeriod).apply {
                    add(Calendar.MONTH, -monthsPeriod)
                }
                getPaymentDateForPeriod(prevPeriodStart, monthsPeriod, paymentDay)
            } else {
                // Иначе текущий платёж уже прошёл или сегодня — день платежа
                currentPayment
            }
        }

        private fun alignToPeriodEnd(today: Calendar, monthsPeriod: Int, paymentDay: Int): Calendar {
            val currentPayment = getPaymentDateForPeriod(today, monthsPeriod, paymentDay)

            return if (today.before(currentPayment)) {
                // Если сегодня до платежа в текущем периоде, следующий платёж — в текущем периоде
                currentPayment
            } else {
                // Иначе следующий платёж — в следующем периоде
                val nextPeriodStart = getPeriodStart(today, monthsPeriod).apply {
                    add(Calendar.MONTH, monthsPeriod)
                }
                getPaymentDateForPeriod(nextPeriodStart, monthsPeriod, paymentDay)
            }
        }

        // ===== Вспомогательные методы (новые, для чистоты логики) =====
        /**
         * Возвращает дату платежа для периода, в котором находится referenceDate.
         * Период: группировка месяцев с шагом monthsPeriod, начиная с месяца 0 (январь).
         * Платёж — в последний месяц периода, в день paymentDay (если такого дня нет — последний день месяца).
         */
        private fun getPaymentDateForPeriod(referenceDate: Calendar, monthsPeriod: Int, paymentDay: Int): Calendar {
            val periodStart = getPeriodStart(referenceDate, monthsPeriod)
            val lastMonth = periodStart.clone() as Calendar
            lastMonth.add(Calendar.MONTH, monthsPeriod - 1)

            val maxDay = lastMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
            val actualDay = if (paymentDay > maxDay) maxDay else paymentDay
            lastMonth.set(Calendar.DAY_OF_MONTH, actualDay)
            return lastMonth
        }

        /**
         * Возвращает начало периода (первый день первого месяца), в котором находится referenceDate.
         * Пример: monthsPeriod=3, referenceDate=15.05.2022 -> 01.04.2022.
         */
        private fun getPeriodStart(referenceDate: Calendar, monthsPeriod: Int): Calendar {
            val result = referenceDate.clone() as Calendar
            val currentMonth = result.get(Calendar.MONTH)
            val periodStartMonth = (currentMonth / monthsPeriod) * monthsPeriod
            result.set(Calendar.MONTH, periodStartMonth)
            result.set(Calendar.DAY_OF_MONTH, 1)
            return result
        }
    }
}