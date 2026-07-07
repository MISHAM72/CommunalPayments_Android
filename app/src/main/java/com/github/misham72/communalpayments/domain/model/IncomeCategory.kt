package com.github.misham72.communalpayments.domain.model

enum class IncomeCategory(val order: Int) {
    SALARY(0),
    DEPOSIT_INTERESTS(1),
    INVESTMENTS(2),
    GIFTS(3),
    SIDE_JOB(4),
    OTHER(5);
}
