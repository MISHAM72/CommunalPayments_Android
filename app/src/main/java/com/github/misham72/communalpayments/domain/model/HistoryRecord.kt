package com.github.misham72.communalpayments.domain.model

/**
 * Одна запись из истории платежей.
 *
 * @param rawBlock    оригинальный текст блока (включая разделитель 🟩×12),
 *                    используется для поиска и замены блока в файле
 * @param dateTime    дата-время из блока ("2026-08-13 22:00:16")
 * @param serviceKey  ключ услуги ("hostel", "electricity", ...)
 * @param attachments список вложений (может быть пустым)
 *
 * Распарсенные поля (заполняются в HistoryParser):
 * @param status          статус оплаты (🔴 / ⏳ / ✅ ...)
 * @param amount          сумма ("К оплате: ...")
 * @param tariff          тариф
 * @param currentReading  текущие показания
 * @param previousReading предыдущие показания
 * @param consumption     расход
 * @param nextPayment     дата следующего платежа
 * @param periodMonths    период в месяцах
 */
data class HistoryRecord(
    val rawBlock: String,
    val dateTime: String,
    val serviceKey: String,
    val attachments: List<Attachment> = emptyList(),

    // Распарсенные поля
    val status: String = "",
    val amount: String = "",
    val tariff: String = "",
    val currentReading: String = "",
    val previousReading: String = "",
    val consumption: String = "",
    val nextPayment: String = "",
    val periodMonths: String = "",
) {
    val hasAttachments: Boolean get() = attachments.isNotEmpty()
}
