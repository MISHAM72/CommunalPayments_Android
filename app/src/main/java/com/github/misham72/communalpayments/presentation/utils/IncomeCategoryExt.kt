package com.github.misham72.communalpayments.presentation.utils

import androidx.annotation.StringRes
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.domain.model.IncomeCategory

@StringRes
fun IncomeCategory.nameRes(): Int = when (this) {
    IncomeCategory.SALARY -> R.string.salary
    IncomeCategory.DEPOSIT_INTERESTS -> R.string.deposit_interests
    IncomeCategory.INVESTMENTS -> R.string.investments
    IncomeCategory.GIFTS -> R.string.gifts
    IncomeCategory.SIDE_JOB -> R.string.side_job
    IncomeCategory.OTHER -> R.string.other
}
