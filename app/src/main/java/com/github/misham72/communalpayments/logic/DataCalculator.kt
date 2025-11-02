package com.github.misham72.communalpayments.logic

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
/**  Вычисляет предыдущие/следующие даты платежей. Считает количество дней между датами
Работает с календарем и временными промежутками. Форматирует даты в строки */
class DateCalculator {

    companion object {
        //Добавьте в DateCalculator методы для получения уже отформатированных строк:
        fun getPreviousPaymentString(monthsPeriod: Int, paymentDay: Int): String {
            val date = getPreviousPayment(monthsPeriod, paymentDay)
            return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date)
        }

        fun getNextPaymentString(monthsPeriod: Int, paymentDay: Int): String {
            val date = getNextPayment(monthsPeriod, paymentDay)
            return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date)
        }


        fun getPreviousPayment(monthsPeriod: Int, paymentDay: Int): Date {
            val calendar = Calendar.getInstance()
            return alignToPeriodStart(calendar, monthsPeriod, paymentDay).time
        }


        fun getNextPayment(monthsPeriod: Int, paymentDay: Int): Date {
            val calendar = Calendar.getInstance()
            return alignToPeriodEnd(calendar, monthsPeriod, paymentDay).time
        }

        fun calculateDaysFromPreviousPayment(monthsPeriod: Int, paymentDay: Int): Long {
            val today = Calendar.getInstance()
            val previousPayment =
                alignToPeriodStart(today.clone() as Calendar, monthsPeriod, paymentDay)

            val diff = today.timeInMillis - previousPayment.timeInMillis
            return diff / (24 * 60 * 60 * 1000)
        }

        private fun alignToPeriodStart(
            today: Calendar,
            monthsPeriod: Int,
            paymentDay: Int
        ): Calendar {
            val periodStart = today.clone() as Calendar

            // Переход к началу периода
            val currentMonth = today.get(Calendar.MONTH)
            val periodStartMonth = (currentMonth / monthsPeriod) * monthsPeriod
            periodStart.set(Calendar.MONTH, periodStartMonth)
            periodStart.set(Calendar.DAY_OF_MONTH, 1)

            // Специальные условия
            when {
                monthsPeriod == 1 && paymentDay == 30 -> periodStart.add(Calendar.DAY_OF_MONTH, -1)
                monthsPeriod == 1 && paymentDay == 23 -> periodStart.add(Calendar.DAY_OF_MONTH, -8)
                monthsPeriod == 3 -> periodStart.add(Calendar.DAY_OF_MONTH, -1)
                monthsPeriod == 12 && paymentDay == 27 -> periodStart.add(
                    Calendar.DAY_OF_MONTH,
                    -66
                )

                monthsPeriod == 12 && paymentDay == 24 -> periodStart.add(Calendar.DAY_OF_MONTH, 54)
            }

            return periodStart
        }


        fun calculateDaysToNextPayment(monthsPeriod: Int, paymentDay: Int): Long {
            val today = Calendar.getInstance()
            val nextPayment = alignToPeriodEnd(today.clone() as Calendar, monthsPeriod, paymentDay)

            val diff = nextPayment.timeInMillis - today.timeInMillis
            return diff / (24 * 60 * 60 * 1000) // Конвертация в дни
        }

        private fun alignToPeriodEnd(
            today: Calendar,
            monthsPeriod: Int,
            paymentDay: Int
        ): Calendar {
            val periodEnd = today.clone() as Calendar

            // Переход к концу периода
            val currentMonth = today.get(Calendar.MONTH)
            val periodStartMonth = (currentMonth / monthsPeriod) * monthsPeriod
            periodEnd.set(Calendar.MONTH, periodStartMonth + monthsPeriod - 1)
            periodEnd.set(
                Calendar.DAY_OF_MONTH,
                minOf(paymentDay, periodEnd.getActualMaximum(Calendar.DAY_OF_MONTH))
            )

            // Специальные условия
            when {
                monthsPeriod == 12 && paymentDay == 27 -> periodEnd.add(Calendar.DAY_OF_MONTH, -61)
                monthsPeriod == 12 && paymentDay == 24 -> periodEnd.add(Calendar.DAY_OF_MONTH, 62)
            }

            return periodEnd
        }


    }
}