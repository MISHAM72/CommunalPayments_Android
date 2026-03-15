package com.github.misham72.communalpayments.presentation.mapper

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.domain.model.PaymentStatus

object StatusDisplayMapper {

    data class DisplayInfo(
        @param:StringRes val textResId: Int,
        val emojiResId: Int,
        @param:ColorRes val colorResId: Int
    )

    fun map(status: PaymentStatus): DisplayInfo {
        return when (status) {
            PaymentStatus.PAID -> DisplayInfo(
                // R.string.status_text_paid — это просто
                // число (например, 2131165327), которое указывает на строку в ресурсах
                textResId = R.string.status_text_paid,
                emojiResId = R.string.emoji_paid,
                colorResId = R.color.status_red
            )

            PaymentStatus.PENDING -> DisplayInfo(
                textResId = R.string.status_text_pending,
                emojiResId = R.string.emoji_pending,
                colorResId = R.color.status_yellow
            )

            PaymentStatus.ACTIVE -> DisplayInfo(
                textResId = R.string.status_text_active,
                emojiResId = R.string.emoji_active,
                colorResId = R.color.status_green
            )

            PaymentStatus.UNDER_REVIEW -> DisplayInfo(
                textResId = R.string.status_text_under_review,
                emojiResId = R.string.emoji_under_review,
                colorResId = R.color.status_blue
            )

            PaymentStatus.CANCELLED -> DisplayInfo(
                textResId = R.string.status_text_cancelled,
                emojiResId = R.string.emoji_cancelled,
                colorResId = R.color.status_black
            )
            // Новый статус
            PaymentStatus.CALCULATED -> DisplayInfo(
                textResId = R.string.status_calculated,  // строка с эмодзи и текстом
                emojiResId = R.string.emoji_calculated, // если у вас отдельный эмодзи, иначе можно взять ту же строку
                colorResId = R.color.status_purple // подберите цвет, создайте в colors.xml
            )
        }
    }
}
