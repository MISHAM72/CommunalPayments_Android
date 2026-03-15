package com.github.misham72.communalpayments.domain.model

enum class PaymentStatus {
    PAID,          // Оплачено
    PENDING,       // Ожидает оплаты
    ACTIVE,        // Активно
    UNDER_REVIEW,  // На проверке
    CANCELLED,      // Отменено

    CALCULATED   // Подсчитано
}
/**
enum class PaymentStatus(val emoji: String) {
PAID("🔴"),
PENDING("🟡"),
ACTIVE("🟢"),
UNDER_REVIEW("🔵"),
CANCELLED("⚫");
}*/
