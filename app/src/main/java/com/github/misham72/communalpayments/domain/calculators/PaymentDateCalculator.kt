package com.github.misham72.communalpayments.domain.calculators

import java.util.Date

interface PaymentDateCalculator {
    fun getPreviousPaymentDate(monthsPeriod: Int, paymentDay: Int, startDate: Date): Date
    fun getNextPaymentDate(monthsPeriod: Int, paymentDay: Int, startDate: Date): Date
    fun getDaysFromPreviousPayment(monthsPeriod: Int, paymentDay: Int, startDate: Date): Long
    fun getDaysToNextPayment(monthsPeriod: Int, paymentDay: Int, startDate: Date): Long
}